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

@description('Name of the service. This name is used to add "azd-service-name" tag to the tags for the container app. Default value is "srping-boot". If you change this value, make sure to change the name of the service in "azure.yaml" file as well.')
param serviceName string = 'spring-boot'

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

@description('URL of the super hero service to call.')
param superHeroUrl string

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

resource springBootIdentity 'Microsoft.ManagedIdentity/userAssignedIdentities@2023-01-31' = {
  name: identityName
  location: location
}

resource applicationInsights 'Microsoft.Insights/components@2020-02-02' existing = {
  name: applicationInsightsName
}

resource keyVault 'Microsoft.KeyVault/vaults@2021-06-01-preview' existing = {
  name: keyVaultName 
}

module springBootKeyVaultAccess '../core/security/keyvault-access.bicep' = {
  name: 'keyvault-access-${springBootIdentity.name}'
  params: {
    keyVaultName: keyVault.name
    principalId: springBootIdentity.properties.principalId
  }
}

module springBoot '../core/host/container-app-upsert.bicep' = {
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
          name: 'SPRING_DATASOURCE_URL'
          value: 'jdbc:postgresql://${databaseConfig.hostname}:${databaseConfig.?port ?? 5432}/${databaseConfig.name}'
        }
        {
          name: 'SPRING_DATASOURCE_USERNAME'
          value: databaseConfig.username
        }
        {
          name: 'SPRING_DATASOURCE_PASSWORD'
          secretRef: 'postgres-admin-password'
        }
        {
          name: 'CLIENT_SUPERHERO_URL'
          value: superHeroUrl
        }
        {
          name: 'APPLICATIONINSIGHTS_CONNECTION_STRING'
          value: applicationInsights.properties.ConnectionString
        }
      ]
      secrets: [
        {
          name: 'postgres-admin-password'
          keyVaultUrl: '${keyVault.properties.vaultUri}secrets/postgres-admin-password'
          identity: springBootIdentity.id
        }
      ]
      targetPort: 8080
    }
    dependsOn: [
      springBootKeyVaultAccess
    ]    
}

/******************************************************************************/
/*                                  OUTPUTS                                   */
/******************************************************************************/

@description('ID of the service principal that is used by the container app to pull image from the container registry.')
output SERVICE_SPRING_BOOT_IDENTITY_PRINCIPAL_ID string = springBootIdentity.properties.principalId

@description('Name of the container app.')
output SERVICE_SPRING_BOOT_NAME string = springBoot.outputs.name

@description('URI of the container app.')
output SERVICE_SPRING_BOOT_URI string = springBoot.outputs.uri

@description('Name of the container apps image.')
output SERVICE_SPRING_BOOT_IMAGE_NAME string = springBoot.outputs.imageName
