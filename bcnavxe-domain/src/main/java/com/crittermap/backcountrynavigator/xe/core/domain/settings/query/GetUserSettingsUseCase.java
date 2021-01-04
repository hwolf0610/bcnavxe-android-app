package com.crittermap.backcountrynavigator.xe.core.domain.settings.query;

import android.support.annotation.Nullable;
import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.data.ISettingsRepository;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.base.AbstractSingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class GetUserSettingsUseCase extends AbstractSingleUseCase {
    private final String TAG = GetUserSettingsUseCase.class.getSimpleName();

    private ISettingsRepository userSettingsRepository;

    @Inject
    public GetUserSettingsUseCase(ISettingsRepository userSettingsRepository,
                                  ThreadExecutor threadExecutor,
                                  PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userSettingsRepository = userSettingsRepository;
    }

    public Single<SettingsDTO> build() {
        return build(null);
    }

    @Override
    protected Single<SettingsDTO> build(@Nullable Object params) {
        return userSettingsRepository.get().doOnSuccess(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO o) {
                Log.d(TAG, "Finish get user settings");
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                Log.e(TAG, "Error on get user settings: " + throwable.getMessage());
            }
        });
    }

    public void execute(final Consumer<SettingsDTO> onSuccess, final Consumer<Throwable> onError) {
        Single<SettingsDTO> single = this.build()
                .subscribeOn(threadExecutor.getScheduler())
                .observeOn(postExecutionThread.getScheduler());
        addDisposable(single.subscribe(new Consumer<SettingsDTO>() {
            @Override
            public void accept(SettingsDTO settings) throws Exception {
                onSuccess.accept(settings);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (!allDisposed()) onError.accept(throwable);
            }
        }));
    }

    public SettingsDTO fetchSettingsDTO() {
        return userSettingsRepository.getSettingDTO();
    }
}
