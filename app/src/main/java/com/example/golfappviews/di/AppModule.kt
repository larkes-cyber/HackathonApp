package com.example.golfappviews.di

import android.content.Context
import com.chaquo.python.Python
import com.example.golfappviews.data.repository.DataGolfRepository
import com.example.golfappviews.data.source.GolfPythonDataSource
import com.example.golfappviews.data.source.GolfPythonDataSourceImpl
import com.example.golfappviews.domain.repository.GolfRepository
import com.example.golfappviews.util.InternetConnectionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCoroutineContext():CoroutineContext{
        return Job()
    }

    @Singleton
    @Provides
    fun provideCoroutineScope(
        context:CoroutineContext
    ): CoroutineScope {
        return CoroutineScope(context + SupervisorJob())
    }



    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun providePythonInstance():Python = Python.getInstance()

    @Provides
    fun provideInternetConnectionService(context: Context) = InternetConnectionService(context)

    @Singleton
    @Provides
    fun provideGolfPythonDataSource(pythonInstance: Python):GolfPythonDataSource = GolfPythonDataSourceImpl(
        pythonInstance = pythonInstance
    )

    @Provides
    @Singleton
    fun provideGolfRepository(golfPythonDataSource: GolfPythonDataSource):GolfRepository{
        return DataGolfRepository(golfPythonDataSource)
    }



}