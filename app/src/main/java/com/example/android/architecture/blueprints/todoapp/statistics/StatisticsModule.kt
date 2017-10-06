package com.example.android.architecture.blueprints.todoapp.statistics

import com.example.android.architecture.blueprints.todoapp.di.ActivityScoped
import com.example.android.architecture.blueprints.todoapp.di.FragmentScoped
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a>
 */
@Module
abstract class StatisticsModule {

    @FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun statisticsFragment(): StatisticsFragment

    @ActivityScoped
    @Binds
    internal abstract fun statisticsPresenter(presenter: StatisticsPresenter): StatisticsContract.Presenter

}