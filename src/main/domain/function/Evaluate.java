package main.domain.function;

import main.domain.Assessment;

import java.util.Collection;
import java.util.HashSet;

import static main.domain.function.BetAssessment.betAssessment;
import static main.domain.function.DoubleDownAssessment.doubleDownAssessment;
import static main.domain.function.InsuranceAssessment.insuranceAssessment;
import static main.domain.function.OutcomeAssessment.outcomeAssessment;
import static main.domain.function.SplitAssessment.splitAssessment;

public class Evaluate {

    public static Collection<Assessment> transactionEvaluators() {
        final Collection<Assessment> evaluators = new HashSet<>();

        evaluators.add(doubleDownAssessment());
        evaluators.add(insuranceAssessment());
        evaluators.add(outcomeAssessment());
        evaluators.add(betAssessment());
        evaluators.add(splitAssessment());

        return evaluators;
    }
}
