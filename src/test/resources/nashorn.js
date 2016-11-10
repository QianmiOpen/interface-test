var testSuit= function () {
    return {
        "dubboServiceURL": "dubbo://172.19.65.199:20880",
        "execOrder": 1,
        "intfName": "com.qianmi.pc.api.app.AppProductProvider:1.0.0@getGoodsStockList",
        "testCases": [
            {
                "name": "case1",
                "params": [
                    {
                        "chainMasterId": "A1463955",
                        "goodsIdList": null,
                        "optUserCode": "A1463955",
                        "optUserName": "lstlyz138",
                        "pageNum": 0,
                        "pageSize": 10,
                        "productName": "",
                        "skuBn": "",
                        "specString": null,
                        "stockWarnEndTime": null,
                        "stockWarnEnum": "WARN",
                        "stockWarnStartTime": null
                    }
                ],
                "expects": [
                    {
                        "operator": "=",
                        "path": "$",
                        "value": {
                            "dataList": [

                            ],
                            "totalCount": 0,
                            "pageNum": 0,
                            "pageSize": 10
                        }
                    }
                ]
            }
        ],
        "testServerURL": null
    };
}();