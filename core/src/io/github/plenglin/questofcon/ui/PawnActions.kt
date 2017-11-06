package io.github.plenglin.questofcon.ui

import com.badlogic.gdx.Input
import io.github.plenglin.questofcon.Constants
import io.github.plenglin.questofcon.game.grid.WorldCoords
import io.github.plenglin.questofcon.game.pawn.Pawn
import io.github.plenglin.questofcon.render.ShadeSet
import io.github.plenglin.questofcon.screen.GameScreen
import ktx.app.KtxInputAdapter
import ktx.app.color


object PawnActionManager {

    private var primaryShadeSet: ShadeSet = ShadeSet(emptySet())

    private var hoveringShadeSet: ShadeSet = ShadeSet(emptySet())
    var movementSquares: Map<WorldCoords, Int> = mapOf()
    var attackSquares = setOf<WorldCoords>()

    var state = PawnActionState.NONE
    var pawn: Pawn? = null

    fun beginMoving(pawn: Pawn) {
        UI.pawnTooltip.isVisible = true
        if (pawn != this.pawn || state != PawnActionState.MOVE) {
            cleanAction()
            this.pawn = pawn
            movementSquares = pawn.getMovableSquares()
            primaryShadeSet = ShadeSet(
                    movementSquares.keys,
                    mode = ShadeSet.SHADE,
                    shading = Constants.movementColor
            )
            GameScreen.shadeSets.add(primaryShadeSet)
            state = PawnActionState.MOVE
        }
    }

    fun attemptFinishMoving(coords: WorldCoords): Boolean {
        val pawn = pawn!!
        if (pawn.moveTo(coords, movementSquares)) {
            cleanAction()
            return true
        } else {
            return false
        }
    }

    fun beginAttacking(pawn: Pawn) {
        UI.pawnTooltip.isVisible = true
        if (pawn != this.pawn || state != PawnActionState.ATTACK) {
            cleanAction()
            this.pawn = pawn
            attackSquares = pawn.getAttackableSquares()
            primaryShadeSet = ShadeSet(
                    attackSquares,
                    mode = ShadeSet.SHADE or ShadeSet.OUTLINE,
                    shading = Constants.attackColor
            )
            GameScreen.shadeSets.add(primaryShadeSet)
            state = PawnActionState.ATTACK
        }
    }

    fun attemptFinishAttacking(coords: WorldCoords): Boolean {
        val pawn = pawn!!
        if (attackSquares.contains(coords) && pawn.attemptAttack(coords)) {
            cleanAction()
            return true
        } else {
            return false
        }
    }

    fun setTargetingRadius(coords: WorldCoords) {
        GameScreen.shadeSets.remove(hoveringShadeSet)
        hoveringShadeSet = ShadeSet(
                PawnActionManager.pawn!!.getTargetingRadius(coords),
                mode = ShadeSet.INNER_LINES,
                shading = Constants.attackColor,
                lines = Constants.attackColor
        )
        GameScreen.shadeSets.add(hoveringShadeSet)
    }

    fun cleanAction() {
        this.pawn = null
        state = PawnActionState.NONE
        GameScreen.shadeSets.remove(hoveringShadeSet)
        GameScreen.shadeSets.remove(primaryShadeSet)
        UI.pawnTooltip.isVisible = false
    }

}


enum class PawnActionState {
    NONE, MOVE, ATTACK
}

object PawnActionInputProcessor : KtxInputAdapter {

    override fun keyDown(keycode: Int): Boolean {
        val pawn = GridSelectionInputManager.selection?.tile?.pawn ?: return false
        if (pawn.team != GameScreen.gameState.getCurrentTeam() || pawn.apRemaining <= 0) {
            return false
        }
        when (keycode) {
            Input.Keys.Q -> {  // Attack
                if (pawn.attacksRemaining > 0 && pawn.actionPoints > 0) {
                    PawnActionManager.beginAttacking(pawn)
                }
            }
            Input.Keys.E -> {  // Move
                if (pawn.actionPoints > 0) {
                    PawnActionManager.beginMoving(pawn)
                }
            }
            Input.Keys.ESCAPE -> {  // Stop what you're doing!
                PawnActionManager.cleanAction()
                return false
            }
        }

        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button != Input.Buttons.LEFT || PawnActionManager.state == PawnActionState.NONE) {
            return false
        }
        val hovering = GridSelectionInputManager.hovering ?: return false
        when (PawnActionManager.state) {
            PawnActionState.MOVE -> {
                PawnActionManager.attemptFinishMoving(hovering)
                return true
            }
            PawnActionState.ATTACK -> {
                PawnActionManager.attemptFinishAttacking(hovering)
                return true
            }
            else -> return false
        }
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        if (PawnActionManager.state == PawnActionState.ATTACK) {
            val hovering = GridSelectionInputManager.hovering
            if (hovering != null && PawnActionManager.attackSquares.contains(hovering)) {
                PawnActionManager.setTargetingRadius(hovering)
            }
        }
        return false
    }
}