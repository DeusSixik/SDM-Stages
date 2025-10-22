# SDM Stages
The mod adds the implementation of stages such as in the Game Stages mod, but with its own features.

The main reason for creating this mod was that the GameStages mod is not ported by the original author, and the 
KubeJs solution does not satisfy us because of the need to drag the mod along for the sake of one mechanic.

The special feature is that our implementation has both a global and a local container of stages, and the data is stored as a 
`UUID -> List<String>`, which allows you to change or add stages even when the player is not on the server.