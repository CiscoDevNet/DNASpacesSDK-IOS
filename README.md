# Cisco DNA Spaces SDK for IOS

This package contains the Binary file for OpenRoaming SDK.

## Adding the SDK package to your project
* On Xcode, go to File > Swift Packages > Add Package Dependency
* On Choose Project window, select which project you want to install OpenRoaming SDK package and click Next
* On Choose Package Repository window, insert the **HTTPS** of the repository where OpenRoaming SDK is located and click Next. Example:
    * https://github.com/CiscoDevNet/DNASpacesSDK-IOS.git
> It might take a while for Xcode to verify the repository.
* After Xcode verified the repository, at the Choose Package Options screen, inform the release version of the package or branch where the package it is and click Next. Example: 
    * For the HTTPS above the package is located in the `master` branch, so you should select Branch and type its name on the available field.
> It might take a while for Xcode to verify the package, so you will not be able to add these informations immediately. 
* Then Xcode will open the Add Package to App screen and after a few minutes, OpenRoaming SDK package will be installed in your App.
> Also, please make sure that OpenRoaming Package is also added on Build Phases > Link Binary with Libraries.

## Test whether the SDK was installed correctly
* Go to your project
* Import `OpenRoaming` on your swift file
* Call the show version method
* `let version = OpenRoaming.showVersion()`
* Check the “version” value. If it is null, redo all the procedures above, otherwise it is working correctly.

## Removing the SDK package from your project
If the installation was not successful, to redo the procedures you maybe will have to remove the package from your project.

* On the File Manager, click on your project `.xcodeproj` file
* Go to Swift Packages
* You will see OpenRoaming, such as its Version Rules and Location
* Select OpenRoaming and click on  — below
