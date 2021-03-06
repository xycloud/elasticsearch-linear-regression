/*
 * Copyright (c) 2017 Scaleborn UG, www.scaleborn.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scaleborn.linereg.calculation.statistics;

/**
 * Created by mbok on 19.03.17.
 */
public class StatsCalculator {

  public Statistics calculate(final StatsModel model) {
    final int featuresCount = model.getStatsSampling().getFeaturesCount();
    final double[] featuresResponseCovariance = model.getStatsSampling()
        .getFeaturesResponseCovariance();
    final double[][] covarianceLowerTriangularMatrix = model.getStatsSampling()
        .getCovarianceLowerTriangularMatrix();
    final double[] slopeCoefficients = model.getSlopeCoefficients().getCoefficients();

    double squaredError = model.getStatsSampling().getResponseVariance();

    for (int i = 0; i < featuresCount; i++) {
      final double c = slopeCoefficients[i];
      final double c2 = c * c;
      // Minus double of feature response coefficient
      squaredError -= 2 * featuresResponseCovariance[i] * c;

      // Add values from covariance matrix of the derivation matrix
      for (int j = 0; j <= i; j++) {
        if (i == j) {
          // Variance term
          squaredError += c2 * covarianceLowerTriangularMatrix[i][j];
        } else {
          // Covariance term
          squaredError += 2 * c * slopeCoefficients[j] * covarianceLowerTriangularMatrix[i][j];
        }
      }
    }
    final double rss = squaredError;
    return new Statistics() {
      @Override
      public double getRss() {
        return rss;
      }

      @Override
      public double getMse() {
        return rss / model.getStatsSampling().getCount();
      }
    };
  }
}
