// swift-tools-version:5.3
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "OpenRoaming",
    products: [
        .library(
            name: "OpenRoaming",
            targets: ["OpenRoaming"]),
    ],
    targets: [
        .binaryTarget(name: "OpenRoaming", path: "OpenRoaming.xcframework")
    ]
)
