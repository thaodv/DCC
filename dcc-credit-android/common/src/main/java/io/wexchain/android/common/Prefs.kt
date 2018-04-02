package io.wexchain.android.common

import android.content.SharedPreferences
import android.os.Build
import android.annotation.TargetApi
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.util.ArraySet


/**
 * Created by lulingzhi on 2017/11/22.
 */

abstract class Prefs protected constructor(protected val sp: SharedPreferences) {

    fun clear(vararg prefs: Pref) {
        val editor = sp.edit()
        for (f in prefs) {
            editor.remove(f.name)
        }
        editor.apply()
    }

    fun clearAll() {
        sp.edit().clear().apply()
    }

    abstract inner class Pref protected constructor(@param:NonNull val name: String) {

        open fun clear() {
            sp.edit().remove(name).apply()
        }

        abstract fun putOp(value: Any?): BatchOp.Op

        fun deleteOp(): BatchOp.Op {
            return DeleteOp()
        }

        private inner class DeleteOp : BatchOp.Op {
            override fun execute(editor: SharedPreferences.Editor) {
                editor.remove(name)
            }
        }
    }

    abstract inner class ObjPref<T> @JvmOverloads constructor(@NonNull name: String, val defaultValue: T? = null) : Pref(name) {

        @Nullable
        abstract fun get(): T?

        abstract fun set(value: T)
    }

    inner class BoolPref(@NonNull name: String, private val defaultValue: Boolean) : Pref(name) {

        fun get(): Boolean {
            return sp.getBoolean(this.name, defaultValue)
        }

        fun set(value: Boolean) {
            sp.edit().putBoolean(name, value).apply()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            if (value is Boolean) {
                val v = (value as Boolean?)!!
                return object : BatchOp.Op {
                    override fun execute(editor: SharedPreferences.Editor) {
                        editor.putBoolean(name, v)
                    }
                }
            }
            throw IllegalArgumentException()
        }
    }

    inner class IntPref(name: String, private val defaultValue: Int) : Pref(name) {

        fun get(): Int {
            return sp.getInt(this.name, defaultValue)
        }

        fun set(value: Int) {
            sp.edit().putInt(name, value).apply()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            if (value is Int) {
                val v = (value as Int?)!!
                return object : BatchOp.Op {
                    override fun execute(editor: SharedPreferences.Editor) {
                        editor.putInt(name, v)
                    }
                }
            }
            throw IllegalArgumentException()
        }
    }

    inner class FloatPref(name: String, defaultValue: Int) : Pref(name) {
        private val defaultValue: Float

        init {
            this.defaultValue = defaultValue.toFloat()
        }

        fun get(): Float {
            return sp.getFloat(this.name, defaultValue)
        }

        fun set(value: Float) {
            sp.edit().putFloat(name, value).apply()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            if (value is Float) {
                val v = (value as Float?)!!
                return object : BatchOp.Op {
                    override fun execute(editor: SharedPreferences.Editor) {
                        editor.putFloat(name, v)
                    }
                }
            }
            throw IllegalArgumentException()
        }
    }

    inner class LongPref(name: String, private val defaultValue: Long) : Pref(name) {

        fun get(): Long {
            return sp.getLong(this.name, defaultValue)
        }

        fun set(value: Long) {
            sp.edit().putLong(name, value).apply()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            if (value is Long) {
                val v = (value as Long?)!!
                return object : BatchOp.Op {
                    override fun execute(editor: SharedPreferences.Editor) {
                        editor.putLong(name, v)
                    }
                }
            }
            throw IllegalArgumentException()
        }
    }

    inner class StringPref : ObjPref<String> {
        constructor(name: String) : super(name, null) {}
        constructor(name: String, defaultValue: String) : super(name, defaultValue) {}

        override fun get(): String? {
            return sp.getString(name, defaultValue)
        }

        override fun set(value: String) {
            sp.edit().putString(name, value).apply()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            if (value is String) {
                val v = value as String?
                return object : BatchOp.Op {
                    override fun execute(editor: SharedPreferences.Editor) {
                        editor.putString(name, v)
                    }
                }
            }
            throw IllegalArgumentException()
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    inner class StringSetPref : ObjPref<Set<String>> {
        constructor(name: String, defaultValue: Set<String>) : super(name, defaultValue) {}
        constructor(name: String) : super(name) {}

        override fun putOp(value: Any?): BatchOp.Op {
            throw UnsupportedOperationException()
        }

        @Nullable
        override fun get(): Set<String>? {
            return sp.getStringSet(name, defaultValue)
        }

        override fun set(set: Set<String>) {
            sp.edit().putStringSet(name, set).apply()
        }

        fun add(value: String) {
            var stringSet: MutableSet<String>? = get()?.toMutableSet()
            if (stringSet == null) {
                stringSet = createSet()
            }
            stringSet.add(value)
            set(stringSet)
        }
    }

    private fun<T> createSet(): MutableSet<T> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArraySet()
        } else {
            android.support.v4.util.ArraySet()
        }
    }

    inner class CachedPref<T>(private val pref: ObjPref<T>) : ObjPref<T>(pref.name, pref.defaultValue) {
        private var value: T? = null

        init {
            this.value = pref.get()
        }

        override fun get(): T? {
            if (value == null) {
                value = pref.get()
            }
            return value
        }

        override fun set(value: T) {
            update(value)
        }


        fun update(value: T?) {
            if (value != null && value != this.value) {
                this.value = value
                pref.set(value)
            }
        }

        override fun clear() {
            value = null
            pref.clear()
        }

        override fun putOp(value: Any?): BatchOp.Op {
            return pref.putOp(value)
        }
    }

    enum class OpType {
        DELETE_VALUE,
        SET_OR_ADD
    }

    fun batch(): BatchOp.Builder {
        return BatchOp.Builder(this)
    }

    /**
     * 批量执行动作
     */
    class BatchOp(private val prefs: Prefs, private val opList: ArrayList<Op>) {

        fun execute() {
            val editor = prefs.sp.edit()
            for (op in opList) {
                op.execute(editor)
            }
            editor.apply()
        }

        fun newBuilder(): Builder {
            val builder = Builder(prefs)
            for (op in opList) {
                builder.addOp(op)
            }
            return builder
        }

        class Builder(private val prefs: Prefs) {
            private val opList: ArrayList<Op>

            init {
                this.opList = ArrayList()
            }

            fun addOp(@NonNull pref: Pref,
                      @NonNull opType: OpType,
                      @Nullable value: Any?): Builder {
                val op = createOp(pref, opType, value)
                return addOp(op)
            }

            @NonNull
            private fun createOp(pref: Pref, opType: OpType, value: Any?): Op {
                when (opType) {
                    Prefs.OpType.DELETE_VALUE -> return pref.deleteOp()
                    Prefs.OpType.SET_OR_ADD -> return pref.putOp(value)
                    else -> throw IllegalArgumentException("unknown op type: " + opType)
                }
            }

            @NonNull
            fun addOp(op: Op): Builder {
                this.opList.add(op)
                return this
            }

            fun delete(@NonNull pref: Pref): Builder {
                return addOp(pref, OpType.DELETE_VALUE, null)
            }

            fun setOrAdd(@NonNull pref: Pref, value: Any): Builder {
                return addOp(pref, OpType.SET_OR_ADD, value)
            }

            fun build(): BatchOp {
                return BatchOp(prefs, opList)
            }

            fun execute() {
                build().execute()
            }
        }

        interface Op {
            fun execute(editor: SharedPreferences.Editor)
        }
    }
}