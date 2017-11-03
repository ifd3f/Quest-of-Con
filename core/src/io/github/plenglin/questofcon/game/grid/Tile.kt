package io.github.plenglin.questofcon.game.grid

import io.github.plenglin.questofcon.game.Team
import io.github.plenglin.questofcon.game.building.Building
import io.github.plenglin.questofcon.game.pawn.Pawn

/**
 *
 */
class Tile(var terrain: Terrain) {

    var pawn: Pawn? = null
    var building: Building? = null

    fun getTeam(): Team? {
        return pawn?.team ?: building?.team
    }

    fun canBuildOn(team: Team): Boolean {
        return building == null && pawn?.let { it.team == team } != false
    }

    fun doDamage(hp: Int): Boolean {
        val bldg = building
        var output = false

        if (bldg != null) {
            bldg.health -= hp
            output = true
        }

        if (pawn != null) {
            println(pawn!!.team.name)
            pawn!!.health -= hp
            output = true
        }
        return output
    }

}