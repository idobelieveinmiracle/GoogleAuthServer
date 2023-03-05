package com.rose.di

import com.rose.data.repository.UserDataSourceImpl
import com.rose.domain.repository.UserDataSource
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val koinModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("user_database")
    }
    single<UserDataSource> {
        UserDataSourceImpl(get())
    }
}