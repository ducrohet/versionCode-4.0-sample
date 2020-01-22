# versionCode-4.0-sample
 
This small sample shows the beginning of the new 
variant API in the android gradle plugin.

File buildSrc/src/main/java/com/example/build/CustomPlugin.kt shows
the creation of 2 tasks that compute the version code and version name
and then wires these task outputs to the variants version code and name.