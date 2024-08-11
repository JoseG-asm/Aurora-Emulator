package com.project_aurora.emu.coreutils

import kotlinx.coroutines.*

data class Task(var name: String, var act: suspend () -> Unit, var type: DispatchersType) {
    suspend fun executeTask() {
        when (type) {
            DispatchersType.IO -> withContext(Dispatchers.IO) { act.invoke() }
            DispatchersType.MAIN -> withContext(Dispatchers.Main) { act.invoke() }
            DispatchersType.DEFAULT -> withContext(Dispatchers.Default) { act.invoke() }
        }
    }
}

interface ProviderTask {
    val tasks: MutableMap<String, Task>
    
    fun newCoroutine(name: String, act: suspend () -> Unit, type: DispatchersType) {
        tasks[name] = Task(name, act, type)
    }
    
    fun executeCoroutine(name: String) {
        val task = tasks[name] ?: return
        GlobalScope.launch {
            task.executeTask()
        }
    }
}


enum class DispatchersType {
    IO,
    MAIN,
    DEFAULT
}

class AsyncTask : ProviderTask {
    
    override val tasks: MutableMap<String, Task> = mutableMapOf()
    
    override fun newCoroutine(name: String, act: suspend () -> Unit, type: DispatchersType) {
        super.newCoroutine(name, act, type)
    }
    
    override fun executeCoroutine(name: String) {
        super.executeCoroutine(name)
    }
}