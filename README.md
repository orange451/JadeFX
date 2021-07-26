## JadeFX
Client Application Platform for Desktop and Mobile. Develop Desktop applications utilising GLFW and Mobile applications utilising GLFM. JadeFX can be run with a standard JDK/JVM using LWJGL3 or with MiniJVM. JadeFX can be incorporated in to your already existing OpenGL project, and/or it can be used to manage all your windowing and rendering code.

[![](https://jitpack.io/v/orange451/JadeFX.svg)](https://jitpack.io/#orange451/JadeFX)

(This library is heavily under development)

- For mobile deployment use MiniJVM: https://github.com/digitalgust/miniJVM

- For desktop deployment use LWJGL: https://www.lwjgl.org/

- MiniJVM ios xcode project: https://github.com/orange451/jadefx_minijvm_ios

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

## Sample Maven POM XML
<details><summary>MiniJVM (GLFW/GLFM) Sample Maven POM (Click)</summary>

	<?xml version="1.0" encoding="UTF-8"?>
	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	    <modelVersion>4.0.0</modelVersion>
	    <groupId>org.minijvm</groupId>
	    <artifactId>JadeFX</artifactId>
	    <version>1.0-SNAPSHOT</version>
	    <packaging>jar</packaging>
	    <properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<maven.compiler.source>1.8</maven.compiler.source>
		<maven.compiler.target>1.8</maven.compiler.target>
	    </properties>

		<repositories>
			<repository>
			    <id>jitpack.io</id>
			    <url>https://jitpack.io</url>
			</repository>
		</repositories>

	    <dependencies>
			<dependency>
			    <groupId>com.github.orange451</groupId>
			    <artifactId>minijvm_rt</artifactId>
			    <version>1.1</version>
			</dependency>

			<dependency>
			    <groupId>com.github.orange451</groupId>
			    <artifactId>glfw_gui</artifactId>
			    <version>1.1</version>
			</dependency>

			<dependency>
				<groupId>org.joml</groupId>
				<artifactId>joml</artifactId>
				<version>1.9.25</version>
			</dependency>	
	    </dependencies>
	</project>
</details>
<details><summary>LWJGL Sample Maven POM (Click)</summary>

	<?xml version="1.0" encoding="UTF-8"?>
	<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	    <modelVersion>4.0.0</modelVersion>
	    <groupId>io.jadefx</groupId>
	    <artifactId>JadeFX</artifactId>
	    <version>1.0-SNAPSHOT</version>
	    <packaging>jar</packaging>
	    <properties>
			<lwjgl.version>3.2.3</lwjgl.version>
			<lwjgl.natives>natives-windows</lwjgl.natives>
	    </properties>

		<repositories>
			<repository>
			    <id>jitpack.io</id>
			    <url>https://jitpack.io</url>
			</repository>
		</repositories>

		<dependencyManagement>
			<dependencies>
				<dependency>
					<groupId>org.lwjgl</groupId>
					<artifactId>lwjgl-bom</artifactId>
					<version>${lwjgl.version}</version>
					<scope>import</scope>
					<type>pom</type>
				</dependency>
			</dependencies>
		</dependencyManagement>

	    <dependencies>
			<dependency>
				<groupId>com.github.orange451</groupId>
				<artifactId>minijvm_desktop_glfm</artifactId>
				<version>1.2</version>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-assimp</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-bgfx</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-cuda</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-egl</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-glfw</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-jawt</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-jemalloc</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-libdivide</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-llvm</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-lmdb</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-lz4</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-meow</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nanovg</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nfd</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nuklear</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-odbc</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-openal</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opencl</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opengl</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opengles</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-openvr</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opus</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-ovr</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-par</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-remotery</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-rpmalloc</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-shaderc</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-sse</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-stb</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tinyexr</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tinyfd</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tootle</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-vma</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-vulkan</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-xxhash</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-yoga</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-zstd</artifactId>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-assimp</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-bgfx</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-glfw</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-jemalloc</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-libdivide</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-llvm</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-lmdb</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-lz4</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-meow</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nanovg</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nfd</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-nuklear</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-openal</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opengl</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opengles</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-openvr</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-opus</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-ovr</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-par</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-remotery</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-rpmalloc</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-shaderc</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-sse</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-stb</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tinyexr</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tinyfd</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-tootle</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-vma</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-xxhash</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-yoga</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.lwjgl</groupId>
				<artifactId>lwjgl-zstd</artifactId>
				<classifier>${lwjgl.natives}</classifier>
			</dependency>
			<dependency>
				<groupId>org.joml</groupId>
				<artifactId>joml</artifactId>
				<version>1.9.25</version>
			</dependency>
	    </dependencies>
	</project>
</details>
