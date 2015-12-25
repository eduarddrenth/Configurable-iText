/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint.report.itext.style.stylers.binding;

import com.vectorprint.configuration.binding.parameters.ParamFactoryValidator;
import com.vectorprint.configuration.binding.parameters.ParameterizableBindingFactory;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ChartFactoryValidator implements ParamFactoryValidator {
   
   private static boolean disabled = true;

   @Override
   public boolean isValid(ParameterizableBindingFactory bindingFactory) {
      return disabled || bindingFactory instanceof ChartParamBindingFactory || bindingFactory instanceof ChartParamBindingFactoryJson;
   }

   public static void setEnabled(boolean enabled) {
      ChartFactoryValidator.disabled = !enabled;
   }

}
