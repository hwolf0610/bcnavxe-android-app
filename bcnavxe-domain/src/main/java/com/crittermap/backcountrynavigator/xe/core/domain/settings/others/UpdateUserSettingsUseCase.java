package com.crittermap.backcountrynavigator.xe.core.domain.settings.others;

import android.util.Log;

import com.crittermap.backcountrynavigator.xe.core.api.dto.SettingsDTO;
import com.crittermap.backcountrynavigator.xe.core.data.ISettingsRepository;
import com.crittermap.backcountrynavigator.xe.core.domain.PostExecutionThread;
import com.crittermap.backcountrynavigator.xe.core.domain.ThreadExecutor;
import com.crittermap.backcountrynavigator.xe.core.domain.base.AbstractSingleUseCase;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class UpdateUserSettingsUseCase extends AbstractSingleUseCase<SettingsDTO> {
    private final String TAG = UpdateUserSettingsUseCase.class.getSimpleName();

    private ISettingsRepository userSettingsRepository;

    @Inject
    public UpdateUserSettingsUseCase(ISettingsRepository userSettingsRepository,
                                     ThreadExecutor threadExecutor,
                                     PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    protected Single<SettingsDTO> build(final SettingsDTO settings) {
        return userSettingsRepository.updateOrInsert(settings)
                .doOnSuccess(new Consumer<SettingsDTO>() {
                    @Override
                    public void accept(SettingsDTO settings) {
                        Log.d(TAG, "Finish update user settings");
                    }
                }).doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        Log.e(TAG, "Error on update user settings: " + throwable.getMessage());
                    }
                });
    }

}
