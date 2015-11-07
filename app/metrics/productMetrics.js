angular.module('metricsModule', [])
    .factory('MetricsService', function ($http, $q) {
        return {
            getProductMetric: function (productId) {

                return $http.get('api/metrics/' + productId + '/count').then(function (response) {
                    return response.data.value;
                });

            }
        };
    });