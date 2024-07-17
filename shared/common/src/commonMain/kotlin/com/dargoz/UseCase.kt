package com.dargoz

abstract class UseCase<Return, Param> {

    suspend operator fun invoke(param: Param): Result<Return> {
        return try {
            Result.success(execute(param))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    abstract suspend fun execute(param: Param): Return

}