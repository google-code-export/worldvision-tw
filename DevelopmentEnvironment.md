## Setup environment ##
  * Ruby (1.8.7)
windows: http://dl.bintray.com/oneclick/rubyinstaller/rubyinstaller-1.8.7-p374.exe?direct
  * Oracle JDK 1.6
windows (jdk-6u35-windows-i586.exe):
http://www.oracle.com/technetwork/java/javase/downloads/jdk6u35-downloads-1836443.html
  * Google App Engine for Java (appengine-java-sdk-1.4.0)
    * http://mirrors.ibiblio.org/maven2/com/google/appengine/appengine-java-sdk/1.4.0/appengine-java-sdk-1.4.0.zip
  * Eclipse for Java EE developer
windows: http://www.eclipse.org/downloads/download.php?file=/technology/epp/downloads/release/kepler/SR2/eclipse-jee-kepler-SR2-win32.zip
    * ruby development tools plugin
  * Git
http://msysgit.github.io/
  * Install Gem
```
gem install google-appengine -v 0.0.17 bundle
```

## Download source code ##
  * Follow the [url](http://code.google.com/p/worldvision-tw/source/checkout) to download the source code

## Development Environment ##
  * Run your eclipse and load the following 2 projects from the code base
```
worldvision-ruby
worldvision-java
```

## How to deploy to google app engine ##
```
cd worldvision-ruby
appcfg.rb update .
```
Try to login to the following url:
```
http://worldvision-tw-stag.appspot.com/
```

## Further Reading ##
  * [app-jruby](http://code.google.com/p/appengine-jruby/wiki/GettingStarted)
  * [data-mapper](http://datamapper.org/)
  * [google app engine for java](https://developers.google.com/appengine/docs/java/overview)