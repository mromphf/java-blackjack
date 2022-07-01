package main.domain.function;

import main.domain.model.Snapshot;
import main.domain.model.Transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

import static main.domain.function.BetAssessment.betAssessment;
import static main.domain.function.DoubleDownAssessment.doubleDownAssessment;
import static main.domain.function.InsuranceAssessment.insuranceAssessment;
import static main.domain.function.OutcomeAssessment.outcomeAssessment;
import static main.domain.function.SplitAssessment.splitAssessment;

public class Evaluate {

    public static Collection<Function<Snapshot, Optional<Transaction>>> transactionEvaluators() {
        final Collection<Function<Snapshot, Optional<Transaction>>> evaluators = new HashSet<>();

        evaluators.add(doubleDownAssessment());
        evaluators.add(insuranceAssessment());
        evaluators.add(outcomeAssessment());
        evaluators.add(betAssessment());
        evaluators.add(splitAssessment());

        return evaluators;
    }
}
