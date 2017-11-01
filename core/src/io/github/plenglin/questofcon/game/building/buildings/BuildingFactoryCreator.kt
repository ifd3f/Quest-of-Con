package io.github.plenglin.questofcon.game.building.buildings

import com.badlogic.gdx.graphics.Color
import io.github.plenglin.questofcon.game.GameData
import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.building.BuildingCreator
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.ui.Selectable
import io.github.plenglin.questofcon.ui.UI
import io.github.plenglin.questofcon.ui.UnitSpawningDialog

class BuildingFactory(team: Team, pos: WorldCoords) : Building("factory", team, pos, 10, Color.GRAY) {

    override fun getActions(): List<Selectable> {
        return super.getActions() + if (pos.tile!!.pawn == null) listOf(
                object : Selectable("Make") {
                    override fun onSelected(x: Float, y: Float) {
                        UI.stage.addActor(UnitSpawningDialog(GameData.spawnableUnits, UI.skin, pos, team))
                    }
                }
        ) else emptyList()
    }

    companion object : BuildingCreator("factory", 20) {

        override fun createBuildingAt(team: Team, worldCoords: WorldCoords): Building {
            val building = BuildingFactory(team, worldCoords)
            worldCoords.tile!!.building = building
            return building
        }

    }
}