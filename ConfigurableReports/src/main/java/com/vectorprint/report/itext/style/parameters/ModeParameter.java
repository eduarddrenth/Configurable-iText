/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vectorprint.report.itext.style.parameters;

import com.vectorprint.configuration.parameters.ParameterImpl;
import com.vectorprint.report.itext.style.stylers.SimpleColumns;

/**
 *
 * @author Eduard Drenth at VectorPrint.nl
 */
public class ModeParameter extends ParameterImpl<SimpleColumns.MODE>{

   public ModeParameter(String key, String help) {
      super(key, help, SimpleColumns.MODE.class);
   }

}
