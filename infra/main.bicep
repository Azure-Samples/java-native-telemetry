targetScope = 'subscription'

// See also https://learn.microsoft.com/en-us/azure/developer/azure-developer-cli/make-azd-compatible?pivots=azd-create

/******************************************************************************/
/*                                 PARAMETERS                                 */
/******************************************************************************/

@minLength(1)
@maxLength(64)
@description('Name of the the environment which is used to generate a short unique hash used in all resources.')
param environmentName string

@minLength(1)
@description('Primary location for all resources')
param location string

/******************************* Resource Names *******************************/

// Optional parameters to override the default azd resource naming conventions.
// Add to main.parameters.json to provide values.

@maxLength(90)
@description('Name of the resource group to deploy. If not specified, a name will be generated.')
param resourceGroupName string = ''

@maxLength(63)
@description('Keyvault resource name. If not specified, a name will be generated.')
param keyVaultName string = ''

@description('Id of the user or app to assign application roles to. AZD will set it to the current user if not specified.')
param principalId string = ''

@maxLength(60)
@description('Name of the container apps environment to deploy. If not specified, a name will be generated. The maximum length is 60 characters.')
param containerAppsEnvironmentName string = ''

@maxLength(50)
@description('Name of the Container Registry to deploy. If not specified, a name will be generated. The name is global and must be unique within Azure. The maximum length is 50 characters.')
param containerRegistryName string = ''

/* Observability  */

@maxLength(63)
@description('Name of the Log Analytics Workspace to deploy. If not specified, a name will be generated. The maximum length is 63 characters.')
param logAnalyticsWorkspaceName string = ''

@maxLength(255)
@description('Name of the Application Insights to deploy. If not specified, a name will be generated. The maximum length is 255 characters.')
param applicationInsightsName string = ''

@maxLength(160)
@description('Name of the Application Insights dashboard to deploy. If not specified, a name will be generated. The maximum length is 160 characters.')
param applicationInsightsDashboardName string = ''

/* PostgreSQL  */

@maxLength(63)
@description('Name of the PostgreSQL flexible server to deploy. If not specified, a name will be generated. The name is global and must be unique within Azure. The maximum length is 63 characters. It contains only lowercase letters, numbers and hyphens, and cannot start nor end with a hyphen.')
param postgresFlexibleServerName string = ''

@maxLength(63)
@description('Name of the PostgreSQL flexible server database to deploy.')
param postgresDatabaseName string = 'database'

@description('Name of the PostgreSQL admin user.')
param postgresAdminUsername string = 'pgadmin'

@secure()
@description('PostGreSQL Server administrator password')
param postgresAdminPassword string

/* Quarkus Telemetry */

@maxLength(32)
@description('Name of the Quarkus Telemetry Container App to deploy. If not specified, a name will be generated. The maximum length is 32 characters.')
param quarkusContainerAppName string = ''

@description('Set if the Quarkus Telemetry Container aAp already exists.')
param quarkusAppExists bool = false

/* Spring Boot Telemetry */

@maxLength(32)
@description('Name of the SpringBoot Telemetry Container App to deploy. If not specified, a name will be generated. The maximum length is 32 characters.')
param springBootContainerAppName string = ''

@description('Set if the SpringBoot Telemetry Container aAp already exists.')
param springBootAppExists bool = false

/******************************************************************************/
/*                                 VARIABLES                                  */
/******************************************************************************/

var abbreviations = loadJsonContent('./abbreviations.json')

// tags that should be applied to all resources.
var tags = {
  // Tag all resources with the environment name.
  'azd-env-name': environmentName
}

/******************************* Resource Names *******************************/


// Generate a unique token to be used in naming resources.
// Remove linter suppression after using.
#disable-next-line no-unused-vars
var resourceToken = toLower(uniqueString(subscription().id, environmentName, location))

@description('Name of the environment with only alphanumeric characters. Used for resource names that require alphanumeric characters only.')
var alphaNumericEnvironmentName = replace(replace(environmentName, '-', ''), ' ', '')

var _containerAppsEnvironmentName = !empty(containerAppsEnvironmentName) ? containerAppsEnvironmentName : take('${abbreviations.appManagedEnvironments}${environmentName}', 60)
var _containerRegistryName = !empty(containerRegistryName) ? containerRegistryName : take('${abbreviations.containerRegistryRegistries}${take(alphaNumericEnvironmentName, 35)}${resourceToken}', 50)
var _logAnalyticsName = !empty(logAnalyticsWorkspaceName) ? logAnalyticsWorkspaceName : take('${abbreviations.operationalInsightsWorkspaces}${environmentName}', 63)
var _applicationInsightsName = !empty(applicationInsightsName) ? applicationInsightsName : take('${abbreviations.insightsComponents}${environmentName}', 255)
var _applicationInsightsDashboardName = !empty(applicationInsightsDashboardName) ? applicationInsightsDashboardName : take('${abbreviations.portalDashboards}${environmentName}', 160)
var _quarkusContainerAppName = !empty(quarkusContainerAppName) ? quarkusContainerAppName : take('${abbreviations.appContainerApps}quarkus-${environmentName}', 32)
var _springBootContainerAppName = !empty(springBootContainerAppName) ? springBootContainerAppName : take('${abbreviations.appContainerApps}spring-book-${environmentName}', 32)
var _postgresFlexibleServerName = !empty(postgresFlexibleServerName) ? postgresFlexibleServerName : take(toLower('${abbreviations.dBforPostgreSQLServers}${take(environmentName, 44)}-${resourceToken}'), 63)
var _keyVaultName = !empty(keyVaultName) ? keyVaultName : take('${abbreviations.keyVaultVaults}${take(alphaNumericEnvironmentName, 8)}${resourceToken}', 24)
var _keyVaultSecrets = [
  {
    name: 'postgres-admin-password'
    value: postgresAdminPassword
  }
]
// Name of the service defined in azure.yaml
// A tag named azd-service-name with this value should be applied to the service host resource, such as:
//   Microsoft.Web/sites for appservice, function
// Example usage:
//   tags: union(tags, { 'azd-service-name': apiServiceName })
#disable-next-line no-unused-vars
var quarkusServiceName = 'quarkus'

#disable-next-line no-unused-vars
var springBootServiceName = 'spring-boot'

/******************************************************************************/
/*                                 RESOURCES                                  */
/******************************************************************************/

// Organize resources in a resource group
resource resourceGroup 'Microsoft.Resources/resourceGroups@2021-04-01' = {
  name: !empty(resourceGroupName) ? resourceGroupName : '${abbreviations.resourcesResourceGroups}${environmentName}'
  location: location
  tags: tags
}

module quarkus './app/quarkus.bicep' = {
  name: 'quarkus'
  scope: resourceGroup
  params: {
    name: _quarkusContainerAppName
    location: location
    tags: tags
    identityName: _quarkusContainerAppName
    applicationInsightsName: monitoring.outputs.applicationInsightsName
    containerAppsEnvironmentName: containerAppsEnvironment.outputs.name
    containerRegistryName: containerRegistry.outputs.name
    databaseConfig: {
      name: 'quarkusdb'
      hostname: postgresServer.outputs.POSTGRES_DOMAIN_NAME
      username: postgresAdminUsername
    }
    keyVaultName: keyVault.name
    exists: quarkusAppExists
  }
}

module springBoot './app/spring-boot.bicep' = {
  name: 'spring-boot'
  scope: resourceGroup
  params: {
    name: _springBootContainerAppName
    location: location
    tags: tags
    identityName: _springBootContainerAppName
    applicationInsightsName: monitoring.outputs.applicationInsightsName
    containerAppsEnvironmentName: containerAppsEnvironment.outputs.name
    containerRegistryName: containerRegistry.outputs.name
    databaseConfig: {
      name: 'springbootdb'
      hostname: postgresServer.outputs.POSTGRES_DOMAIN_NAME
      username: postgresAdminUsername
    }
    keyVaultName: keyVault.name
    exists: springBootAppExists
  }
}

module containerAppsEnvironment 'core/host/container-apps-environment.bicep' = {
  name: _containerAppsEnvironmentName

  scope: resourceGroup
  params: {
    name: _containerAppsEnvironmentName
    location: location
    tags: tags
    logAnalyticsWorkspaceName: monitoring.outputs.logAnalyticsWorkspaceName
    applicationInsightsName: monitoring.outputs.applicationInsightsName
  }
}

module keyVault './core/security/keyvault.bicep' = {
  name: _keyVaultName
  scope: resourceGroup
  params: {
    name: _keyVaultName
    location: location
    tags: tags
    principalId: principalId
  }
}

module principalKeyVaultAccess './core/security/keyvault-access.bicep' = {
  name: 'keyvault-access-${principalId}'
  scope: resourceGroup
  params: {
    keyVaultName: keyVault.outputs.name
    principalId: principalId
  }
}

@batchSize(1)
module keyVaultSecrets './core/security/keyvault-secret.bicep' = [for secret in _keyVaultSecrets: {
  name: 'keyvault-secret-${secret.name}'
  scope: resourceGroup
  params: {
    keyVaultName: keyVault.outputs.name
    name: secret.name
    secretValue: secret.value
  }
}]

module containerRegistry 'core/host/container-registry.bicep' = {
  name: _containerRegistryName
  scope: resourceGroup
  params: {
    name: _containerRegistryName
    location: location
    tags: tags
  }
}

module monitoring 'core/monitor/monitoring.bicep' = {
  name: 'monitoring'
  scope: resourceGroup
  params: {
    location: location
    tags: tags
    logAnalyticsName: _logAnalyticsName
    applicationInsightsName: _applicationInsightsName
    applicationInsightsDashboardName: _applicationInsightsDashboardName
  }
}

module postgresServer 'core/database/postgresql/flexibleserver.bicep' = {
  name: _postgresFlexibleServerName
  scope: resourceGroup
  params: {
    name: _postgresFlexibleServerName
    location: location
    tags: tags
    sku: {
      name: 'Standard_B1ms'
      tier: 'Burstable'
    }
    storage: {
      storageSizeGB: 32
    }
    version: '16'
    administratorLogin: postgresAdminUsername
    administratorLoginPassword: postgresAdminPassword
    databaseNames: [
      'quarkusdb', 'springbootdb'
    ]
    allowAzureIPsFirewall: true
  }
}

/******************************************************************************/
/*                                  OUTPUTS                                   */
/******************************************************************************/

// Add outputs from the deployment here, if needed.
//
// This allows the outputs to be referenced by other bicep deployments in the deployment pipeline,
// or by the local machine as a way to reference created resources in Azure for local development.
// Secrets should not be added here.
//
// Outputs are automatically saved in the local azd environment .env file.
// To see these outputs, run `azd env get-values`,  or `azd env get-values --output json` for json output.

@description('Location where all resources were installed.')
output AZURE_LOCATION string = location

@description('Azure Tenant ID.')
output AZURE_TENANT_ID string = tenant().tenantId

@description('Azure Key Vault name. Is reused to fetch PostgreSQL password in main.parameters.json')
output AZURE_KEY_VAULT_NAME string = keyVault.outputs.name

@description('Container registry endpoint.')
output AZURE_CONTAINER_REGISTRY_ENDPOINT string = containerRegistry.outputs.loginServer

@description('Quarkus application URI.')
output QUARKUS_SERVICE_URI string = quarkus.outputs.SERVICE_QUARKUS_URI

@description('SpringBoot application URI.')
output SPRING_BOOT_SERVICE_URI string = springBoot.outputs.SERVICE_SPRING_BOOT_URI
