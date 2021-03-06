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

package org.scaleborn.linereg.sampling.exact;

import java.io.IOException;
import org.scaleborn.linereg.sampling.ModelSamplingFactory;
import org.scaleborn.linereg.sampling.Sampling.CoefficientLinearTermSampling;
import org.scaleborn.linereg.sampling.Sampling.CoefficientSquareTermSampling;
import org.scaleborn.linereg.sampling.Sampling.InterceptSampling;
import org.scaleborn.linereg.sampling.Sampling.ResponseVarianceTermSampling;
import org.scaleborn.linereg.sampling.io.StateInputStream;
import org.scaleborn.linereg.sampling.io.StateOutputStream;

/**
 * Created by mbok on 26.03.17.
 */
public class ExactModelSamplingFactory implements ModelSamplingFactory<ExactSamplingContext> {

  @Override
  public ExactSamplingContext createContext(final int featuresCount) {
    return new ExactSamplingContext(featuresCount);
  }

  @Override
  public ResponseVarianceTermSampling<?> createResponseVarianceTermSampling(
      final ExactSamplingContext context) {
    return new ExactResponseVarianceTermSampling(context);
  }

  private static class ExactResponseVarianceTermSampling implements
      ResponseVarianceTermSampling<ExactResponseVarianceTermSampling> {

    private final ExactSamplingContext context;

    public ExactResponseVarianceTermSampling(
        final ExactSamplingContext context) {
      this.context = context;
    }

    @Override
    public double getResponseVariance() {
      return this.context.responseSquareSum
          - this.context.responseSum / this.context.getCount() * this.context.responseSum;
    }

    @Override
    public void sample(final double[] featureValues, final double responseValue) {
      // Nothing to sample, covered by ExactSamplingContext
    }

    @Override
    public void merge(final ExactResponseVarianceTermSampling fromSample) {
      // Nothing to merge, covered by ExactSamplingContext
    }

    @Override
    public void saveState(final StateOutputStream destination) {
      // No state
    }

    @Override
    public void loadState(final StateInputStream source) {
      // No state
    }
  }

  @Override
  public CoefficientLinearTermSampling<?> createCoefficientLinearTermSampling(
      final ExactSamplingContext context) {
    return new ExactCoefficientLinearTermSampling(context);
  }

  private static class ExactCoefficientLinearTermSampling implements
      CoefficientLinearTermSampling<ExactCoefficientLinearTermSampling> {

    private final ExactSamplingContext context;

    public ExactCoefficientLinearTermSampling(
        final ExactSamplingContext context) {
      this.context = context;
    }

    @Override
    public double[] getFeaturesResponseCovariance() {
      /**
       * TODO: Migrate to another algorithm to avoid sums of products, which can lead to numerical
       * instability as well as to arithmetic overflow.
       */
      final long count = this.context.getCount();
      final int featuresCount = this.context.getFeaturesCount();
      final double[] covariance = new double[this.context.getFeaturesCount()];
      final double[] featuresMean = this.context.getFeaturesMean();
      final double responseMean = this.context.getResponseMean();
      for (int i = 0; i < featuresCount; i++) {
        covariance[i] =
            this.context.featuresResponseProductSum[i] - featuresMean[i] * this.context.responseSum
                - responseMean * this.context.featureSums[i]
                + count * featuresMean[i] * responseMean;
      }
      return covariance;
    }

    @Override
    public void sample(final double[] featureValues, final double responseValue) {
      // Nothing to sample, covered by ExactSamplingContext
    }

    @Override
    public void merge(final ExactCoefficientLinearTermSampling fromSample) {
      // Nothing to merge, covered by ExactSamplingContext
    }

    @Override
    public void saveState(final StateOutputStream destination) {
      // No state
    }

    @Override
    public void loadState(final StateInputStream source) {
      // No state
    }
  }

  @Override
  public CoefficientSquareTermSampling<?> createCoefficientSquareTermSampling(
      final ExactSamplingContext context) {
    return new ExactCoefficientSquareTermSampling(context);
  }

  @Override
  public InterceptSampling<?> createInterceptSampling(final ExactSamplingContext context) {
    return new ExactInterceptSampling(context);
  }

  private static class ExactInterceptSampling implements InterceptSampling<ExactInterceptSampling> {

    private final ExactSamplingContext context;

    public ExactInterceptSampling(
        final ExactSamplingContext context) {
      this.context = context;
    }

    @Override
    public void saveState(final StateOutputStream destination) throws IOException {
      // No state
    }

    @Override
    public void loadState(final StateInputStream source) throws IOException {
      // No state
    }

    @Override
    public void sample(final double[] featureValues, final double responseValue) {
      // Nothing to do, covered by ExactSamplingContext
    }

    @Override
    public void merge(final ExactInterceptSampling fromSample) {
      // Nothing to sample, covered by ExactSamplingContext
    }

    @Override
    public double[] getFeaturesMean() {
      return this.context.getFeaturesMean();
    }

    @Override
    public double getResponseMean() {
      return this.context.getResponseMean();
    }
  }
}
