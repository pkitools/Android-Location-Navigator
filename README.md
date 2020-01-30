# Android Location Navigator
[![](https://jitpack.io/v/pkitools/Android-Location-Navigator.svg)](https://jitpack.io/#pkitools/Android-Location-Navigator)
An open source and free android library supporting all major navigation apps to get directions and location on several apps.

Note: this isn't another location finder or travel library it only facilitates usage of existing libraries



#How to use
You can clone and checkout master branch of this library and use as a library module in your IDE


## Setup

1- Add jitpack repositories
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add	gradle dependency
```
<dependency>
	    <groupId>com.github.pkitools</groupId>
	    <artifactId>Android-Location-Navigator</artifactId>
	    <version>{Tag/VERSION}</version>
	</dependency>
```
	
After setting up the library, have a look at ```LaunchNavigator``` class. It is the facade for all functionality of this library

```java
LaunchNavigator navigator = LaunchNavigator.with(context);
NavigationParameter parameter = NavigationParameter.builder().app(LaunchNavigator.WAZE)
                                    .destination(Position.builder().latitude(Double.toString(locationBox.getLatitude())).longitude(Double.toString(locationBox.getLongitude()))
                                    .build()).build(); 
navigator.navigate(parameter);
```

## Permission
We need network permission to check if you are connected for our Geo and reverse Geo Coding
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


## Navigation parameters
Navigation parameter is the property sent to '''LocationNavigator''' specifying details of a trip including
- Start Positionand End Position
- Traveling Method
- Navigation App

### Position
You can Setup a destination and start position for navigation.


#### Position Type
You can add a position based on latitude/longitude, title of place or address of it.

#### Geocoding

Geocodes will use google maps API to convert an address to latitude / longitude and reverse

By default Geo Coding is enabled you can disable it by calling
```LaunchNavigator.withoutGeoCoding()```


### Traveling Method
Supported navigation methods are : 
- Drive 
- Bicycle
- Walk
- Public transport 

#### Navigation App 
You can specify your favourite app in the parameter.

### Supported Apps
You can get list of apps supported by calling 
'''getSupportedApps'''
Currently we support most of known navigation providers but please feel free to request if your favourite app is not in list
- Google Maps
- Citymapper
- Uber
- Waze
- Yandex Navigator
- Sygic
- HERE Maps
- Moovit
- Lyft
- MAPS.ME
- Cabify
- Baidu Maps
- 99 Taxi
- Gaode Maps (Amap)


#### Available Apps
List of apps that are installed in user's device and are supported by the library




## Develop


# Contribution
Android Location Navigator (ALN) is still a baby. I just started it and have a long road to go.
It would be much appreciated if any one can help 


### Development Rules

SonarQube is used for checking quality of code.
Please ensure sonarlint plugin is installed in your Android Studio and there is no major or critical quality issues.
For now we have very limited number of tests but moving forward we shall have at least 85% coverage.
Therefore please make sure your changes are covered in tests

. ***Please follow java best practices for naming conventions***

. ***make sure all public and protected libraries are documented in a human readable way***

. ***think twice before a pull request***
 

#### Feature Implementation
This is the process flow for implementing of a feature:
1. Clone project locally
2. Generate a branch
3. Implement the feature
4. Make pull request
5. Code review
6. Merge


List of QA rules to ensure the code is consistent and reliable
Our priorities are:
1. Ease of use
2. Reusability 
3. Readability
4. Functionality

### Known Issues


### Bug Report
Please report your genuine bugs using GitHub bugs


### Road Map

1. stabilising and testing the app
2. improve code quality and test coverage
3. add new location providers
4. add new features
