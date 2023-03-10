package com.rose.di

import com.rose.data.repository.UserDataSourceImpl
import com.rose.domain.repository.UserDataSource
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koinModule = module {
    single {
        KMongo.createClient(System.getenv("MONGO_DB"))
            .coroutine
            .getDatabase("user_database")
    }
    single<UserDataSource> {
        UserDataSourceImpl(get())
    }
}