package main.usecase;


import main.domain.model.Table;

import javax.inject.Inject;

import static main.domain.predicate.LowOrderPredicate.startOfRound;

public class StateRecorder implements TableObserver {

    private final StateRepository stateRepository;

    @Inject
    public StateRecorder(final StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public void onUpdate(Table table) {
        if (startOfRound.test(table)) {
            stateRepository.saveNewRound(table);
        } else {
            stateRepository.saveLastActionTaken(table);
        }
    }
}
