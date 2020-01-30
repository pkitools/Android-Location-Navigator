# Android Location Navigator
An open source and free android library supporting all major navigation apps to get directions and location on several apps.

Note: this isn't another location finder or travel library it only facilitates usage of existing libraries



#How to use


## Setup
You can clone and checkout master branch of this library and use as a library module in your IDE

After setting up the library, have a look at '''LocationNavigator''' class. It is the facade for all functions of this library


## Navigation parameters
Navigation parameter is the property sent to '''LocationNavigator''' specifying details of a trip including
- Start Positionand End Position
- Traveling Method
- Navigation App

### Position
You can Setup a destination and start position for navigation.


#### Position Type
You can add a position based on latitude/longitude, title of place or address of it.


### Traveling Method
Drive, Bicycle, Walk, Public transport are supported navigation methods.

#### Navigation App 
You can specify your favourite app in the parameter.

### Supported Apps
You can get list of apps supported by calling 
'''getSupportedApps'''
Currently we support most of known navigation providers but please feel free to request if your favourite app is not in list

- Google map
- Waze
- Uber


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
