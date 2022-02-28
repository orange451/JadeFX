## JadeFX
Client Application Platform for Desktop and Mobile. Develop Desktop applications utilising GLFW and Mobile applications utilising GLFM. JadeFX can be run with a standard JDK/JVM using LWJGL3 or with MiniJVM. JadeFX can be incorporated in to your already existing OpenGL project, and/or it can be used to manage all your windowing and rendering code.

[![](https://jitpack.io/v/orange451/JadeFX.svg)](https://jitpack.io/#orange451/JadeFX)

## Features
- JavaFX-like scene graph implementation
- CSS Styling (not complete)
- Deployment on both Desktop and Mobile


## Supported Operating Systems
* Windows
* Mac
* Linux
* iOS
* Android

(This library is heavily under development)

- For mobile deployment use MiniJVM: https://github.com/digitalgust/miniJVM

- For desktop deployment use LWJGL: https://www.lwjgl.org/

## Libraries required:
<details><summary>LWJGL3</summary>

* [minijvm_desktop_glfm](https://github.com/orange451/minijvm_desktop_glfm)
	
* [LWJGL3](https://www.lwjgl.org/)
	
* [JOML](https://github.com/JOML-CI/JOML) (Can download with [LWJGL](https://www.lwjgl.org/customize))
	
* [NanoVG](https://github.com/memononen/nanovg) (Can download with [LWJGL](https://www.lwjgl.org/customize))
	
* [TinyFD](https://github.com/native-toolkit/tinyfiledialogs) (Can download with [LWJGL](https://www.lwjgl.org/customize))
</details>

<details><summary>MiniJVM</summary>

* [minijvm_rt](https://github.com/orange451/minijvm_rt)
	
* [glfw_gui](https://github.com/orange451/glfw_gui)
	
* [JOML](https://github.com/JOML-CI/JOML)
</details>

## Include JadeFX in your project
<details><summary>Maven</summary>

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
	
	<dependency>
	    <groupId>com.github.orange451</groupId>
	    <artifactId>JadeFX</artifactId>
	    <version>master-SNAPSHOT</version>
	</dependency>
</details>

<details><summary>Gradle</summary>

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	
	dependencies {
	        implementation 'com.github.orange451:JadeFX:master-SNAPSHOT'
	}
</details>

## Example Projects (Maven POM included)
MiniJVM Examples (Maven)
https://github.com/orange451/JadeFXExampleApplications-MiniJVM
ios xcode project: https://github.com/orange451/jadefx_minijvm_ios

LWJGL Examples (Maven)
https://github.com/orange451/JadeFXExampleApplications-LWJGL


## Pictures
<img width="380" alt="" src="https://user-images.githubusercontent.com/5247778/154409402-234d045a-f796-4436-8a7e-1eda732f035b.png"><img width="340" alt="" src="https://user-images.githubusercontent.com/5247778/154409408-5386fb55-fe6e-4e81-969b-7cd0055689b5.png">

