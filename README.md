# Cisco DNA Spaces SDK for IOS

This package contains the Binary file for Cisco DNA Spaces SDK.

## Adding the SDK package to your project
* On Xcode, go to File > Swift Packages > Add Package Dependency
* On Choose Project window, select which project you want to install Cisco DNA Spaces SDK package and click Next
* •	On Choose Package Repository window, insert the HTTPS of the repository where Cisco DNA Spaces SDK is located (https://github.com/CiscoDevNet/DNASpacesSDK-IOS.git) and click Next. 
  > It might take a while for Xcode to verify the repository.
* After Xcode verified the repository, at the Choose Package Options screen, inform the name of  branch where the package is located: **master**. Click Next.
  > It might take a while for Xcode to verify the package, so you will not be able to add these informations immediately. 
* Xcode will open the Add Package to App screen and after a few minutes, Cisco DNA Spaces SDK package will be installed in your App.
* Please make sure that Cisco DNA Spaces SDK Package is also added on Build Phases > Link Binary with Libraries.

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


## Unable to install "App" error
If you face this error, please check out this link: https://pspdfkit.com/guides/ios/current/knowledge-base/library-not-found-swiftpm/
