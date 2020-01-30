# Android Location Navigator
An open source and free android library supporting all major navigation apps to get directions and location on several apps

#How to use


## Setup
You can clone and checkout master branch of this library and use as a library module in your IDE


## Navigation parameters


### Position
You can Setup a destination and start position for navigation.


### Position Type
You can add a position based on latitude/longitude, title of place or address of it.


### Navigator
Drive, Bicycle, Walk, Public transport are supported navigation methods.


### Supported Apps
You can get list of apps supported by calling 
'''getSupportedApps'''
Currently we support most of known navigation providers but please feel free to request if your favourite app is not in list

- Google map
- Waze
- Uber


### Available Apps
List of apps that are installed in user's device and are supported by the library


#### Navigation  
You can specify your favourite app in the parameter.



## Develop


# Contribution
Android Location Navigator (ALN) is still a baby. I just started it and have a long road to go.
It would be much appreciated if any one can help 


### Development Rules
List of QA rules to ensure the code is consistent and reliable


#### Quality
SonarQube is used for checking quality of code.
Please ensure sonarlint plugin is installed in your Android Studio and there is no major or critical quality issues.
For now we have very limited number of tests but moving forward we shall have at least 85% coverage.
Therefore please make sure your changes are covered in tests


### Known Issues


### Bug Report



### Road Map

1- stabilising and testing the app
2- improve code quality and test coverage
3- add new location providers
4- add new features
