angular.module('productsModule', ['ui.router', 'ngAnimate'])
    .factory('ProductsService', function ($http, $q) {
        return {
            productList: null,

            productsLoaded: function () {
                return !!self.productList;
            },

            getProducts: function () {
                if (this.productsLoaded()) {
                    return $q.when(self.productList);
                } else {
                    console.log("OK unauthenticated, lets make request");
                    return $http.get('app/products/products.json').then(function (response) {
                        self.productList = response.data.products;
                        return self.productList;
                    });
                }
            }
        };

    })
    .controller('productsCtrl', function ($scope, $state, ProductsService) {

        $scope.products = [];

        ProductsService.getProducts().then(function(productList){
            $scope.products = productList;
        });

    });