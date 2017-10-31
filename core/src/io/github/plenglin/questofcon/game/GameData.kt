package io.github.plenglin.questofcon.game

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.building.BuildingFactoryCreator
import io.github.plenglin.questofcon.game.pawn.PawnCreator
import io.github.plenglin.questofcon.game.pawn.SimplePawnCreator


object GameData {
    val spawnableUnits = listOf<PawnCreator>(
            SimplePawnCreator("footman", 10, 3, 2, Color.MAROON),
            SimplePawnCreator("spearman", 10, 5, 2, Color.LIGHT_GRAY)
    )

    val spawnableBuildings = listOf<BuildingCreator>(
            BuildingFactoryCreator()
    )

}