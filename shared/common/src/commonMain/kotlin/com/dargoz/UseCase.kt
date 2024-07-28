package com.dargoz

abstract class UseCase<Return, Param> {

    suspend operator fun invoke(param: Param): IResult<Return> {
        return try {
            IResult.success(execute(param))
        } catch (e: Exception) {
            IResult.failure(e)
        }
    }

    abstract suspend fun execute(param: Param): Return

}