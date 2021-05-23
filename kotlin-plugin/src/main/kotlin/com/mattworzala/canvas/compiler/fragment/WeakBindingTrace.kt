package com.mattworzala.canvas.compiler.fragment

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.com.intellij.util.keyFMap.KeyFMap
import org.jetbrains.kotlin.ir.declarations.IrAttributeContainer
import org.jetbrains.kotlin.psi2ir.generators.GeneratorContext
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice
import org.jetbrains.kotlin.util.slicedMap.WritableSlice
import java.util.*

/**
 * This class is meant to have the shape of a BindingTrace object that could exist and flow
 * through the Psi2Ir -> Ir phase, but doesn't currently exist. Ideally, this gets replaced in
 * the future by a trace that handles this use case in upstream. For now, we are okay with this
 * because the combination of IrAttributeContainer and WeakHashMap makes this relatively safe.
 */
class WeakBindingTrace {
    private val map = WeakHashMap<Any, KeyFMap>()

    fun <K : IrAttributeContainer, V> record(slice: WritableSlice<K, V>, key: K, value: V) {
        var holder = map[key.attributeOwnerId] ?: KeyFMap.EMPTY_MAP
        val prev = holder.get(slice.key)
        if (prev != null) {
            holder = holder.minus(slice.key)
        }
        holder = holder.plus(slice.key, value!!)
        map[key.attributeOwnerId] = holder
    }

    operator fun <K : IrAttributeContainer, V> get(slice: ReadOnlySlice<K, V>, key: K): V? {
        return map[key.attributeOwnerId]?.get(slice.key)
    }
}

private val FragmentTemporaryGlobalBindingTrace = WeakBindingTrace()

@Suppress("unused")
val GeneratorContext.irTrace: WeakBindingTrace
    get() = FragmentTemporaryGlobalBindingTrace
@Suppress("unused")
val GenerationState.irTrace: WeakBindingTrace
    get() = FragmentTemporaryGlobalBindingTrace
@Suppress("unused")
val IrPluginContext.irTrace: WeakBindingTrace
    get() = FragmentTemporaryGlobalBindingTrace