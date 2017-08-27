# EboLogger
A Java-based logging system with optional UI on a workstation to visualize logging data in real-time.

# Benefits
* Can be used almost as a drop-in replacement for Android's Log system with a simple search and replace and a couple of extra instructions.
* Compiles out almost entirely in release builds.
* Uses string formatting instead of string concatenation.
* Fast early-outs for messages below the severity threshold.
* Can connect to app on host for interactive logging, or write to a file.
* UI splits messages by thread, has substring search and filtering.
* Logs additional data like affected object and user-defined markers.
* Optionally logs stack trace per message.
* Automatically logs every Activity switch, and every crash.

# Target platforms

The code is written in (mostly) platform independent Java and design to run on anything. There are a few Android-specific parts in their own package.
However, because they're currently in the same module, and because of some Android-specific annotations, the logger can only be used on Android.

The UI runs on any platform that can run Java applications.

# IMPORTANT NOTE - THIS IS WIP

In case this isn't obvious, this is an early build of a project in development. A lot of stuff is buggy, haphazard, untested, poorly written. Expect the API to change. The UI is sure to undergo major changes. 

# Installation

There is no Maven support yet. For now, you can clone the EboLogger repository into your
project and add this to your top-level `settings.gradle`:

```groovy
include ':EboLogger:ebologger'
```

And this to your project's module:

```groovy
    debugCompile project(path: ':EboLogger:ebologger', configuration: 'devDebug')
    releaseCompile project(path: ':EboLogger:ebologger', configuration: 'prodRelease')
```

# Usage

Each class should create an instance, ideally like this:

```java
class MyClass {
    private final Logger logger = Logger.get(this);
}
```

If you're intending to use it in a static context, then a static instance will do just fine:

```java
class AnyClass {
    private static final Logger logger = Logger.get();
}
```

To log a message, write:

```java
    logger.info().log("This is a %s", noun);
```

Possible severity levels are `debug()`, `verbose()`, `info()`,
`warning()`, `error()`, `wtf()`.

You can insert additional tags between the severity level and the `log()` command:

`exception(Throwable)` adds information about a `Throwable` to the message.

`tag(String)` adds the tag. By default, this is the name of the class in which the Logger object was created (and what will be used when logging to Android's logcat), but can be overridden here.

`marker(String)` adds a marker. This marker will be prominent in the UI and should be used for severe events, like an Activity switch, or a crash.

`object(Object)` associates this message with a certain object. By default, this is the object passed into `Logger.get()`, but it can be overridden here.

# Additional tracking

You can optionally run the following commands:

```java
    CrashHandler.create().install();
```

This will install a handler that automatically sends the stack trace of a fatal crash as it occurs. This handler will not interfere with the handler that was installed previously - it will invoke it once it's done.

```java
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        ActivityTracker.get().register(context);
    }
```

This will install the activity tracker, which automatically logs every lifecycle event of an Activity (onCreate, onStart, onResume, and their counterparts).`

# Converting existing Android logging commands

You can convert your existing calls to Log. The only downside is that you will be using string concatenation, so you won't be able to get the benefit of the performance improvements in release builds.

To convert, simply add this line to the top of each class that contains logging:

```java
class MyClass {
    private final Logger logger = Logger.get(this);
}
```

You can create a static instance if you ever log a message in a static method. Then, replace `Log.d` with `logger.d`, `Log.w` with `logger.w` ,`Log.wtf` with `logger.wtf`, and so on. These functions behave just like their `Log` counterparts, but they'll also interact with the logger system, so the messages will show up in the UI.

# The UI

Since the UI is still in early development, this entire section here is rudimentary, and bound to change a lot. Brace yourself.

The app is currently packaged as a JAR file. To run it, run `java -jar EboLogger.jar` from a command line. You can also build and run the app from IntelliJ (not Android Studio).

Once run, the app will start a listener socket and wait for incoming connections.

An Android app using EboLogger will automatically try to establish a connection as soon it encounters the first logging command (which may happen automatically when you use activity tracking). Details on this connection process below.

# Establishing the connection

Eventually, there will be an option to choose an output stream (write to file, connect to UI, use broadcast, connect to a specific IP address, or nada). For now, the app will always try to connect to the UI. If it detects being run from an emulator, it will use the emulator's special host loopback address, so it should automatically connect to the computer that's running the emulator.
In all other cases, it will send an IPv4 broadcast and connect to whichever IP responds appropriately.

The app has a send buffer to queue up packets of data that will be sent once the connection has been established. Once the buffer is full, it may block for a few seconds until it finally gives up and disables the logging system.

# Release builds

In release builds, most functionality is stripped out. By default, any message below warning (i.e. debug, verbose, info) will not be processed. That means the strings will not be formatted, and they will not be sent to Android logcat.
Anything more severe (warning, error, wtf) will be sent to Android logcat, but nothing more than that.


# Contact
Twitter: [@EboMike][1]

Google+: [EboMike][2]

License
-------
    Copyright 2017 Michael Krehan

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://twitter.com/EboMike
[2]: https://plus.google.com/u/0/108410866291813017116
