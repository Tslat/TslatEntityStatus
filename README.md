# TslatEntityStatus
#### View entity health and other statuses. Make smacking things fun again!

TES is a 'damage-indicators' style mod designed to add vital information to the world about the entities in it.
It aims to encompass as many facets of information as practical, in a fully-configurable manner so that anyone can use it for any information slice they want.

It is also built with third-party integration in mind, allowing for other mods to configure or utilise the systems provided to their fullest extent

### Features
* HUD-style entity status interface
* In-world overhead entity status interface
* In-world damage and healing 'pop-off' particles
* Multi-facet status elements
    * Entity name
    * Entity health
    * Entity armour & toughness
    * Entity type alignments (fire immunity, melee, ranged, etc)
    * Entity potion effect icons
* Fully-user configurable
  * Toggle on/off any element on an individual basis
  * Toggle any feature on/off on an individual basis
  * Adjust the automated features to balance performance to your liking
  * Choose how and when you want the HUDs to appear
  * Configure the render style of the health bar(s)
  * Adjust the size of the on-screen and in-world HUDs
  * Change the default colours of the damage and healing particles
  * Adjust the opacity of the HUDs to your liking
* Public-facing mod API
  * Trigger your own in-world particles of any type
    * Use the defaults or define your own animation style
    * Render as text, numbers, or any other custom rendering style you choose
  * Add your own HUD elements
    * Render whatever information you choose, in your own free space in the existing hud
    * Decide whether you want in-world or just HUD rendering
    * Replace/modify/expand/remove on the builtin or other mods' HUD elements
    * Dynamically determine your element's visibility and usage
  * Easily retrieve TES' config options from anywhere
  * Fully-documented implementation for all dev-facing classes, fields, and methods
  * Mutliloader implementation for all-platform support


### Donate
Want to support what I do? Consider becoming a [Patron!](https://www.patreon.com/Tslat)

--

[![Hosted By: Cloudsmith](https://img.shields.io/badge/OSS%20hosting%20by-cloudsmith-blue?logo=cloudsmith&style=for-the-badge)](https://cloudsmith.com)

Package repository hosting is graciously provided by  [Cloudsmith](https://cloudsmith.com).
Cloudsmith is the only fully hosted, cloud-native, universal package management solution, that
enables your organization to create, store and share packages in any format, to any place, with total
confidence.