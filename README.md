Sport Trackers Connector
=

#Description
Library/API to connect to sport trackers (eg. Garmin Connect, Endomondo, Runtastic).

#Motivation
No free/public and well-documented API for Garmin Connect and Endomondo.

#Installation
Not yet available!
```xml
        <dependency>
            <groupId>pl.raszkowski</groupId>
            <artifactId>sport-trackers-connector</artifactId>
            <version>[LATEST_RELEASE]</version>
        </dependency>
```

#Usage

##Create connector

###Factory method
```java
GarminConnectConnector connector = ConnectorsFactory.createConnector(Connectors.GARMIN_CONNECT);
```

###Straight-forward method
```java
GarminConnectConnector connector = new GarminConnectConnector();
```

##Authorize (optional)
```java
GarminConnectConnector connector = ConnectorsFactory.createConnector(Connectors.GARMIN_CONNECT);

GarminConnectCredentials credentials = new GarminConnectCredentials();
credentials.setUsername("username");
credentials.setPassword("password");

connector.authorize(credentials);
```

##Execute requests
```java
GarminConnectConnector connector = ConnectorsFactory.createConnector(Connectors.GARMIN_CONNECT);

RESTExecutor restExecutor = connector.getRESTExecutor();

String response = restExecutor.executeGET("user-service-1.0", "username); //e.g. JSON response
```

##Execute API methods
```java
GarminConnectConnector connector = ConnectorsFactory.createConnector(Connectors.GARMIN_CONNECT);

GarminAPIHandler apiHandler = connector.getAPIHandler();

JsonArray activities = apiHandler.getActivities(0, 20);
```