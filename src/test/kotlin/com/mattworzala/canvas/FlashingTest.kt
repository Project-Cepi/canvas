@file:JvmName("FlashingTest")

package com.mattworzala.canvas

import com.mattworzala.canvas.extra.mask
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.time.TimeUnit

val WHITE_STAINED_GLASS_PANE = Material.WHITE_STAINED_GLASS_PANE.id

@JvmField
val FlashingInv = fragment(9, 5) {
    var color: Int by useState(0)
    useUpdate(5, TimeUnit.TICK) {
        color = (color + 1) % 16
    }

    useEffect {
        println("Render")
        return@useEffect { println("Cleanup") }
    }

    // Background mask
    mask {
        pattern = """
            000000000
            011111110
            011000110
            011111110
            000000000
        """.trimIndent()

        fill(ItemStack.of(Material.BLACK_STAINED_GLASS_PANE, 1))
    }

    mask {
        pattern = """
            111111111
            100000001
            100000001
            100000001
            111111111
        """.trimIndent()

        fill(ItemStack.of(Material.fromId((WHITE_STAINED_GLASS_PANE + color).toShort()), 1))
    }

    put(BasicCounter, 21)
}