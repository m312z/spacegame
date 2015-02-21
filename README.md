### To set up in Eclipse

* Create a new project.
* Clone the repository, so that "src/" is in the project folder.
* Refresh the project in Eclipse.
* Add the following jars to the build path:
```
lwjgl-xxx/jar/
		jinput.jar
		jogg-xxx.jar
		jorbis-xxx.jar
		lwjgl_util.jar
		lwjgl.jar
		slick-util.jar
```
* In the project properties, "Java Build Path" expand lwjgl.jar.
* Edit the "Native library location", choose workspace, and "SpaceStation/lwjgl-2.8.3/native/windows" (or linux/macosx)
