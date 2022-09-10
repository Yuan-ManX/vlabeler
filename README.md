# vLabeler

[![Discord](https://img.shields.io/discord/984044285584359444?style=for-the-badge&label=discord&logo=discord&logoColor=ffffff&color=7389D8&labelColor=6A7EC2)](https://discord.gg/yrTqG2SrRd)

`vLabeler` is an open-sourced voice labeling application, aiming:

- Modern and fluent UI/UX
- Customizable labeling/import/export, to be used by different types of voice editor apps
- High performance with multiplatform support

**The project is now in Alpha.
For help, suggestions, issue reports, etc. please join our [Discord](https://discord.gg/yrTqG2SrRd).**

## Download

See [Releases](https://github.com/sdercolin/vlabeler/releases).

Packaged application for the following platforms are provided in the releases.

- Windows (.zip/.exe)
- Mac (Apple Silicon or Intel) (.dmg)
- Ubuntu (.deb)

For other types of Linux os, you may have to build it by yourself.

### For macOS users

If you cannot open the app with a "damaged" error, please run the following commands with your terminal.

```
sudo xattr -rc /Applications/vLabeler.app
```

If it doesn't work, try

```
sudo xattr -r -d com.apple.quarantine /Applications/vLabeler.app
```

Note that `sudo` command requires your password.

## Building

vLabeler is built by [Compose Multiplatform](https://github.com/JetBrains/compose-jb). You can use Gradle to build the
application. [See more](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution)

Currently, cross-platform building is not supported. Only packages for your OS are built.

Please ensure you have **JDK 15+** for building.

```
// Package by an installer
./gradlew package

// Or, build an executable app
./gradlew createDistributable
```

## Usage

Many behaviors of vLabeler depend on customizable `labeler`s.
Currently, built-in labelers include `UTAU oto labeler` and `Sinsy (NNSVS/ENUNU) lab labeler`.

#### oto labeler (Basic mode, SetParam style)

![](readme/oto.gif)

There are two items in the built-in labeler list:

- UTAU oto labeler

  If you don't need negative overlap values, please choose this one.

- UTAU oto labeler plus

  This labeler has an extra line named `Left` which is the actual `Left` value used when exporting the oto file. You
  can move the `Ovl` line to the left of `Left` line to get negative overlap values. The white left border is not
  used.

#### lab labeler (Continuous mode)

![](readme/lab.gif)

- Sinsy lab labeler

  This labeler is for Sinsy (NNSVS/ENUNU) lab files. Please note that the accpeted lab format use `100ns` as the time
  unit and use `\t` (tab) as the separator.

## Get started

1. Click `New project..`
2. Select a folder containing your sample files
3. Change `Project location` and `Project name` if you would like
4. Change `Cache directory` if you would like to save the cache files (rendered images, processed wav files, etc.)
   somewhere else
5. Select a labeler (e.g. UTAU oto labeler if you are editing UTAU oto)
6. Select a label file template and its encoding (e.g. a pre-filled oto file), or leave it blank to use the default
   template (not recommended)
7. If you don't have a template file, select a template generator along with an input file that it requires
8. Click `OK` and start editing
9. Click `Export` in the menu to get the edited label file

## Available keyboard/mouse actions

Note that the following `Ctrl` is mapped to `Command` if you are using macOS.

You can customize the key bindings in `Settings` -> `Prefereneces` -> `Keymaps`

### Move parameter lines

- Mouse drag on normal parameters: move itself
- Mouse drag on primary parameters: move all the lines together
- `Shift`: invert normal/primary. e.g. mouse drag on primary parameters with Shift pressed only moves itself.

### Audio playback

- `Space`: play the current entry, or stop playing if already playing
- `Shift` + `Space`: play the current sample file or stop playing if already playing
- `Ctrl` + mouse click: play the clicked section
- `Alt` + mouse drag on parameters: play the audio near the cursor's position while moving

### Scrolling

- `Shift` + mouse wheel scroll: horizontal scroll
- `F` or Focus button in the center of the bottom bar: Scroll to center the current entry on the screen

### Zoom in/out

- `=` or `+` button in the bottom bar: zoom in
- `-` or `-` button in the bottom bar: zoom out
- `Ctrl` + `Shift` + mouse wheel scroll: zoom in/out
- Resolution button in the bottom bar: open dialog for resolution input

### Switch entry/sample

- Mouse wheel scroll: go to previous/next entry
- `Up`/`Down`: go to previous/next entry
- `<`/`>` buttons in the bottom bar: go to previous/next entry
- `Ctrl` + mouse wheel scroll: go to previous/next sample
- `Ctrl` + `Up`/`Down`: go to previous/next sample
- `<<`/`>>` buttons in the bottom bar: go to previous/next sample
- `Ctrl` + `G` or entry number button in the bottom bar: show `Go to entry...` dialog

### Set metadata for entry

- `J` or click the `Add tag` button in the entry title bar: Start editing the entry's tag
- `K` or click the `Star` button in the entry title bar: Toggle the entry's `Starred` status
- `L` or click the `Done` button in the entry title bar: Toggle the entry's `Done` status

#### Settings for metadata

You can customize the metadata display in `Settings` -> `Prefereneces` -> `Editor` -> `Metadata` to hide the items that
you don't need.

The `Done` status is by default automatically set when you edit any values in a entry. You can disable this behavior in
the settings too.

## Multiple editing mode

For labelers with `continuous: true` (currently only the sinsy lab labeler), you can switch between the normal editing
mode and
a multiple editing mode which shows and allows you to edit all the connected entries in the same sample file.
It's enabled by default. You can toggle the menu item `Edit` -> `Edit All Connected Entries` to enable or disable it.

By clicking the name of entries displayed at the top of the editor, the following actions are conducted:

- click: Rename the entry
- long click: Go to the entry

## Browse and filter entries

There are two components showing an entry list:

1. The pinned entry list: shown on the right side of the editor by default. You can toggle it by via menu `View`
   -> `Pin Entry List`. It provides a full powered entry filter (described below).
2. The `Go to entry...` dialog: shown when you press `Ctrl` + `G` or click the entry number button in the bottom bar.
   It provides a simple entry filter.

### Advanced search

In both types of entry list, you can use the following syntax to search entries:

```
aaa;name=bbb;sample=ccc;tag=ddd
```

Multiple conditions can be combined with `;`. Only entries that match all the conditions are shown.
The following condition keys are supported:

- no key: search in the entry name, sample name or tag
- `name`: search in the entry name
- `sample`: search in the sample name
- `tag`: search in the tag

The match type for this search is `contains`.

### Expanded filter

In the pinned entry list, you can click the `Expand` button to show more options.

For the `Done` filter and `Star` filter, clicking will switch them between `Do not filter`, `Show only starred/done`
and `Show only unstarred/undone`.

These filters are combined with the search text with `AND` logic.

### Linking filter to editor

By default, project navigation (go to next sample/entry etc. by keyboard shortcuts, mouse wheel scroll or button click)
is not affected by the filter.

For example, even if you have filtered out entry `no.5` in the entry list, you can still press `Down` key to go to the
entry `no.5` from entry `no.4`.

The `Link` button in the expanded filter can help you concentrate on a specific set of entries. When it's toggled, the
entries that are not shown in the pinned entry list will be skipped when navigating.

In the above example, if you have toggled the `Link` button, you will go to entry `no.6` when pressing `Down` key
instead of`no.5`.

Note that the `Go to entry...` dialog is not affected by the linked filter, so you can still go to any entry by it
without changing or clearing your filter settings in the pinned entry list.

## Tools

The following editing tool is provided.
You can use shortcuts or menu items under `Edit` -> `Tools` to switch tools, or toggle the Toolbox view by menu `View`
-> `Show Toolbox`.

Note that you can use the `Play the clicked section` feature by `Ctrl + Click` with any tool.

### Cursor

The normal cursor tool to drag parameter controllers (lines or labels).

### Scissors

Cut the current entry into two parts by your click position.
By default, when you click on a valid position with the scissors:

1. Audio of the former (left) part after cutting is played so that you can confirm the phoneme
2. A dialog is shown, asking you to rename the former entry
3. Cutting and renaming are conducted
4. The editor navigates to the former entry if needed

### Pan

Or the hand tool. Drag the charts to scroll the canvas.

These actions can be customized in `Settings` -> `Prefereneces`.

## Labelers

A "labeler" is a configuration file for `vLabeler` which defines the behavior of a certain type of singing voice
generation software.
For example, the built-in `UTAU oto labeler` is a labeler for editing UTAU's `oto.ini` files.

A labeler defines:

- what data/fields a voice entry should contain (e.g. for UTAU, you need `fixed`, `pre-utterance`, `overlap`, etc.)
- how the data/fields are displayed in the editor as parameter controllers
- whether entries should be connected (e.g. `Sinsy lab Labeler` does)
- how to parse the label file to a `vLabeler` project
- how to generate the label file from a `vLabeler` project
- and more behaviors when editing the certain type of label files

After the first run, you can find built-in labeler files named `labelers/*.labeler.json` under `.../<user>/vLabeler`
directory. (For macOS it's `~/Library/vLabeler`)
You can edit the labelers, but please
check [LabelerConf.kt](src/jvmMain/kotlin/com/sdercolin/vlabeler/model/LabelerConf.kt) and make sure you understand the
content before you edit them.

If you want to edit the labels for a singing voice generation software that is not supported by `vLabeler`,
instead of requesting development supporting that software, you can create a labeler to make it work. (it requires
some knowledge of the coding though).
After you put a new labeler file under the directory and restart the application, it will be available in
the `New Project` page.
You can distribute the labelers created by yourself to other users.
You are also welcome to submit your labeler files to our project to make it built-in.

You can import/delete/disable labelers in `Settings` -> `Labelers...`.

## Plugins

Currently, two types of plugins are available.

### Template generators

A template generator can help generate project templates from input files and parameters.

Compared to a default template generated by the labeler which is literally minimum to include all the sample files
given, a template generator plugin can create complicated templates. It's also more customizable with user input
parameters.

All valid template generators which support the selected labeler can be chosen on the `New Project` page.

You can import/delete/disable template generators in `Settings` -> `Template Generators...`.

### Batch edit plugins

Batch edit plugins can be used to conduct complicated batch editing tasks.

You can find available batch edit plugins in `Tools` -> `Batch Edit`. Some displayed plugins may not be clickable, if
it is not supported by the current labeler.

You can import/delete/disable batch edit plugins in `Tools` -> `Batch Edit` -> `Manage plugins...`.

### Plugin Development

We welcome plugin development for the application.
You can distribute your plugin anywhere, or create a pull request to make it built-in.

See [Plugin API Document](readme/plugin-development.md) for details.

## Launch Arguments

See [Launch Arguments](readme/launch-arguments.md) for details.

## Logs

Log files are saved under `.../vLabeler/.logs` folder.
You can check the logs for development/debug/test purposes.
When reporting issues, please attach the recent log files.

## Known issues

1. On Linux, the file chooser may not be able to select an empty folder. Please create something inside, or copy + paste
   its path
2. Sometimes the window freezes unexpectedly. You can resize the window to refresh it
