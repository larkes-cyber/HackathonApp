package com.google.mediapipe.examples.poselandmarker.di

import android.content.Context
import android.graphics.Bitmap
import com.chaquo.python.Python
import com.google.mediapipe.examples.poselandmarker.data.repository.DataGolfRepository
import com.google.mediapipe.examples.poselandmarker.data.source.FrameImageDataSource
import com.google.mediapipe.examples.poselandmarker.data.source.FrameImageDataSourceImpl
import com.google.mediapipe.examples.poselandmarker.data.source.GolfPythonDataSource
import com.google.mediapipe.examples.poselandmarker.data.source.GolfPythonDataSourceImpl
import com.google.mediapipe.examples.poselandmarker.domain.repository.GolfRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context) = context

    @Singleton
    @Provides
    fun provideFrameCachingStorage():FrameImageDataSource{
        return FrameImageDataSourceImpl()
    }

    @Singleton
    @Provides
    fun providePythonInstance(): Python = Python.getInstance()

    @Singleton
    @Provides
    fun provideGolfDataSource(
        python: Python
    ):GolfPythonDataSource{
        return GolfPythonDataSourceImpl(
            python = python
        )
    }

    @Singleton
    @Provides
    fun provideGolfRepository(
        golfPythonDataSource: GolfPythonDataSource,
        frameImageDataSource: FrameImageDataSource
    ):GolfRepository = DataGolfRepository(golfPythonDataSource, frameImageDataSource)

}