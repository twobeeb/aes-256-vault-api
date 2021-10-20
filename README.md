## aes-256-vault-api

A simple API to vault passwords without the caller knowing the secret key

## Usage
````console
# Lists the available vaults
curl http://aes-256-vault-api:8080/
> ["f4m", "test"]

# Vault a password against a vault
curl -X POST http://aes-256-vault-api:8080/f4m -d '{"password":"p@ss"}' -H "Content-Type: application/json"
> znkEn3qalsk+TrZKKfohFw==

# Same in plaintext
curl -X POST http://aes-256-vault-api:8080/f4m -d "p@ss" -H "Content-Type: text/plain"
> znkEn3qalsk+TrZKKfohFw==
````
## Configuration

Either mount the following config file in ``/app/application.yml``
````yaml
# /app/application.yml
vaults:
  f4m:
    key: aaaabbbbccccdddd
    salt: p8t42EhY9z2eSUdpGeq7HX7RboMrsJAhUnu3EEJJVS
# You can add as many as you need :
#  team2:
#    key: <team2's key>
#    salt: <team2's salt>
#  other:
#    key: <other's key>
#    salt: <other's salt>
````
or load the application with the equivalent environment properties 
````shell
VAULTS_F4M_KEY=aaaabbbbccccdddd
VAULTS_F4M_SALT=p8t42EhY9z2eSUdpGeq7HX7RboMrsJAhUnu3EEJJVS
````

### K8S deployment example
````yaml
k8s_deployment.yml
---
apiVersion: v1
kind: Service
metadata:
  labels:
    app: aes-256-vault-api
  name: aes-256-vault-api-service
spec:
  selector:
    app: aes-256-vault-api
  ports:
  - name: "8080"
    port: 8080
    targetPort: 8080
---
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  labels:
    app: aes-256-vault-api
  name: aes-256-vault-api-secret
stringData:
  VAULTS_F4M_KEY: aaaabbbbccccdddd
  VAULTS_F4M_SALT: p8t42EhY9z2eSUdpGeq7HX7RboMrsJAhUnu3EEJJVS
#  You can add as many as you need :
#  VAULTS_TEAM2_KEY: <team2's key>
#  VAULTS_TEAM2_SALT: <team2's salt>
#  VAULTS_OTHER_KEY: <other's key>
#  VAULTS_OTHER_SALT: <other's salt>
---
apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: aes-256-vault-api
  name: aes-256-vault-api-deployment
spec:
  selector:
    matchLabels:
      app: aes-256-vault-api
  replicas: 1
  template:
    metadata:
      labels:
        app: aes-256-vault-api
    spec:
      containers:
      - name: aes-256-vault-api
        image: twobeeb/aes-256-vault-api
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
        envFrom:
          - secretRef:
              name: aes-256-vault-api-secret
      restartPolicy: Always

````
