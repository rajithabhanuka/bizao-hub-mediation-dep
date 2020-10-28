/*
 *
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */
package com.wso2telco.dep.mediator;

import org.apache.axis2.addressing.EndpointReference;

 
// TODO: Auto-generated Javadoc

/**
 * The Class OperatorEndpoint.
 */
public class OperatorEndpoint {

    /** The endpointref. */
    EndpointReference endpointref;
    
    /** The operator. */
    String operator;

    /** The operator id */
    private int operatorId;

    /**
     * Instantiates a new operator endpoint.
     *
     * @param endpointref the endpointref
     * @param operator the operator
     */
    public OperatorEndpoint(EndpointReference endpointref, String operator) {
        this.endpointref = endpointref;
        this.operator = operator;
    }

    /**
     * Instantiates a new operator endpoint with id
     *
     * @param endpointref  the endpointref
     * @param operator     the operator
     * @param operatorId   the operator id
     */
    public OperatorEndpoint(EndpointReference endpointref, String operator, int operatorId) {
        this(endpointref, operator);

        this.operatorId = operatorId;
    }
    
    /**
     * Gets the endpointref.
     *
     * @return the endpointref
     */
    public EndpointReference getEndpointref() {
        return endpointref;
    }

    /**
     * Sets the endpointref.
     *
     * @param endpointref the new endpointref
     */
    public void setEndpointref(EndpointReference endpointref) {
        this.endpointref = endpointref;
    }

    /**
     * Gets the operator.
     *
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the operator.
     *
     * @param operator the new operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }


    /**
     * Gets the operator id
     *
     * @return
     */
    public int getOperatorId() {
        return operatorId;
    }

    /**
     * Sets the operator id
     *
     * @param operatorId
     */
    public void setOperatorId(int operatorId) {
        this.operatorId = operatorId;
    }
}
