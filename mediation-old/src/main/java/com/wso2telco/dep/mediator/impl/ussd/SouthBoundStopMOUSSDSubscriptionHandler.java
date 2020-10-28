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

package com.wso2telco.dep.mediator.impl.ussd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wso2telco.dep.mediator.OperatorEndpoint;
import com.wso2telco.dep.mediator.entity.ussd.DeleteOperator;
import com.wso2telco.dep.mediator.entity.ussd.DeleteSubscriptionRequest;
import com.wso2telco.dep.mediator.entity.ussd.DeleteSubscriptionRequestDTO;
import com.wso2telco.dep.mediator.internal.Type;
import com.wso2telco.dep.mediator.internal.UID;
import com.wso2telco.dep.mediator.mediationrule.OriginatingCountryCalculatorIDD;
import com.wso2telco.dep.mediator.service.USSDService;
import com.wso2telco.dep.mediator.util.HandlerUtils;
import com.wso2telco.dep.oneapivalidation.service.IServiceValidate;
import com.wso2telco.dep.oneapivalidation.service.impl.ussd.ValidateUssdCancelSubscription;
import com.wso2telco.dep.operatorservice.model.OperatorEndPointDTO;
import com.wso2telco.dep.operatorservice.model.OperatorSubscriptionDTO;
import com.wso2telco.dep.subscriptionvalidator.util.ValidatorUtils;

import org.apache.axis2.addressing.EndpointReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.json.JsonUtil;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SouthBoundStopMOUSSDSubscriptionHandler implements USSDHandler {

    private USSDExecutor executor;
    private USSDService dbService;
	private OriginatingCountryCalculatorIDD occi;
    private Log log = LogFactory.getLog(SouthBoundStopMOUSSDSubscriptionHandler.class);
    private Gson gson = new GsonBuilder().serializeNulls().create();
	private static final String API_TYPE = "ussd";
    
    public SouthBoundStopMOUSSDSubscriptionHandler(USSDExecutor ussdExecutor) {

        this.executor = ussdExecutor;
        dbService = new USSDService();
        occi = new OriginatingCountryCalculatorIDD();
    }

    @Override
    public boolean validate(String httpMethod, String requestPath, JSONObject jsonBody, MessageContext context)
            throws Exception {
        IServiceValidate validator;

        validator = new ValidateUssdCancelSubscription();
        validator.validateUrl(requestPath);

        return true;
    }

    @Override
    public boolean handle(MessageContext context) throws Exception {
        if (executor.getHttpMethod().equalsIgnoreCase("DELETE")) {
            return deleteSubscriptions(context);
        }
        return false;

    }

    private boolean deleteSubscriptions(MessageContext context) throws Exception {
        UID.getUniqueID(Type.DELRETSUB.getCode(), context, executor.getApplicationid());

        String requestPath = executor.getSubResourcePath();
        Integer subscriptionId = Integer.parseInt((requestPath.substring(requestPath.lastIndexOf("/") + 1)).replaceFirst("sub", ""));
        List<OperatorSubscriptionDTO> domainsubs = (dbService.moUssdSubscriptionQuery(Integer.valueOf(subscriptionId)));

        if (!domainsubs.isEmpty() && domainsubs != null) {

            // If operator list also added as the payload, to be used in HUB
            List<DeleteOperator> deleteOperators = new ArrayList<DeleteOperator>();
            
            for (OperatorSubscriptionDTO domainSub : domainsubs) {
                deleteOperators.add(	new DeleteOperator(
				                        domainSub.getOperator(),
				                        domainSub.getDomain(),
				                        "Bearer " + executor.getAccessToken(domainSub.getOperator(), context),
				                        Integer.valueOf(dbService.getOperatorIdByOperator(domainSub.getOperator())))
                );
            }
            
            DeleteSubscriptionRequest deleteSubscriptionRequest = new DeleteSubscriptionRequest(new DeleteSubscriptionRequestDTO(deleteOperators));

            String payload = gson.toJson(deleteSubscriptionRequest);

            JsonUtil.newJsonPayload(((Axis2MessageContext) context).getAxis2MessageContext(), payload, true, true);

            // First operator is taken into variables to be used in GW
            OperatorSubscriptionDTO sub = domainsubs.get(0);
            HandlerUtils.setHandlerProperty(context, this.getClass().getSimpleName());
            HandlerUtils.setEndpointProperty(context, sub.getDomain());
            HandlerUtils.setAuthorizationHeader(context, executor,
                    new OperatorEndpoint(new EndpointReference(sub.getDomain()), sub.getOperator()));
            context.setProperty("subscriptionId", subscriptionId);
        }
        return true;
    }
}
