targetScope = 'resourceGroup'

/******************************************************************************/
/*                                 PARAMETERS                                 */
/******************************************************************************/

@description('Name of the container app.')
param name string

@description('Location in which the resources will be deployed. Default value is the resource group location.')
param location string = resourceGroup().location

@description('Tags that will be added to all the resources. For Azure Developer CLI, "azd-env-name" should be added to the tags.')
param tags object = {}

@description('Name of the service. This name is used to add "azd-service-name" tag to the tags for the container app. Default value is "quarkus". If you change this value, make sure to change the name of the service in "azure.yaml" file as well.')
param serviceName string = 'quarkus'

@description('Name of the identity that will be created and used by the container app to pull image from the container registry.')
param identityName string

@description('Name of the existing Application Insights instance that will be used by the container app.')
param applicationInsightsName string

@description('Name of the existing container apps environment.')
param containerAppsEnvironmentName string

@description('Name of the existing container registry that will be used by the container app.')
param containerRegistryName string

@description('Database connection configuration information.')
param databaseConfig databaseConfigType

@description('Name of the Key Vault that contains the secrets.')
param keyVaultName string

@description('Flag that indicates whether the container app already exists or not. This is used in container app upsert to set the image name to the value of the existing container apps image name.')
param exists bool

/******************************************************************************/
/*                                   TYPES                                    */
/******************************************************************************/

type databaseConfigType = {
  hostname: string
  name: string
  username: string
  port: int?
}

/******************************************************************************/
/*                                 RESOURCES                                  */
/******************************************************************************/

resource quarkusIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: identityName
  location: location
}

resource applicationInsights 'Microsoft.Insights/components@2020-02-02' existing = {
  name: applicationInsightsName
}

resource keyVault 'Microsoft.KeyVault/vaults@2021-06-01-preview' existing = {
  name: keyVaultName 
}

module quarkusKeyVaultAccess '../core/security/keyvault-access.bicep' = {
  name: 'keyvault-access-${quarkusIdentity.name}'
  params: {
    keyVaultName: keyVault.name
    principalId: quarkusIdentity.properties.principalId
  }
}

module quarkus '../core/host/container-app-upsert.bicep' = {
    name: '${serviceName}-container-app'
    params: {
      name: name
      location: location
      tags: union(tags, { 'azd-service-name': serviceName })
      identityType: 'UserAssigned'
      identityName: identityName
      exists: exists
      containerAppsEnvironmentName: containerAppsEnvironmentName
      containerRegistryName: containerRegistryName
      env: [
        {
          name: 'QUARKUS_DATASOURCE_JDBC_URL'
          value: 'jdbc:postgresql://${databaseConfig.hostname}:${databaseConfig.?port ?? 5432}/${databaseConfig.name}'
        }
        {
          name: 'QUARKUS_DATASOURCE_USERNAME'
          value: databaseConfig.username
        }
        {
          name: 'QUARKUS_DATASOURCE_PASSWORD'
          secretRef: 'postgres-admin-password'
        }
        {
          name: 'QUARKUS_OTEL_AZURE_APPLICATIONINSIGHTS_CONNECTION_STRING'
          value: applicationInsights.properties.ConnectionString
        }
      ]
      secrets: [
        {
          name: 'postgres-admin-password'
          keyVaultUrl: '${keyVault.properties.vaultUri}secrets/postgres-admin-password'
          identity: quarkusIdentity.id
        }
      ]
      targetPort: 8080
    }
    dependsOn: [
      quarkusKeyVaultAccess
    ]
}

/******************************************************************************/
/*                                  OUTPUTS                                   */
/******************************************************************************/

@description('ID of the service principal that is used by the container app to pull image from the container registry.')
output SERVICE_QUARKUS_IDENTITY_PRINCIPAL_ID string = quarkusIdentity.properties.principalId

@description('Name of the container app.')
output SERVICE_QUARKUS_NAME string = quarkus.outputs.name

@description('URI of the container app.')
output SERVICE_QUARKUS_URI string = quarkus.outputs.uri

@description('Name of the container apps image.')
output SERVICE_QUARKUS_IMAGE_NAME string = quarkus.outputs.imageName
