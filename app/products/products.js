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
                    return $http.get('assets/products/products.json').then(function (response) {
                        self.productList = response.data.products;
                        return self.productList;
                    });
                }
            },

            getProduct: function (productId) {
                return $http.get('assets/products/' + productId + '.json').then(function (response) {
                    return response.data.product;
                });
            }
        };

    })
    .controller('productsCtrl', function ($scope, $state, ProductsService) {

        $scope.products = [];

        ProductsService.getProducts().then(function (productList) {
            $scope.products = productList;
        });

    })
    .controller("productCtrl", function ($scope, $state, $timeout) {

        $scope.loading = false;

        $scope.productClick = function () {
            if (!$scope.loading) {
                $scope.loading = true;
                $timeout(function () {
                    $state.go('home.product', {id: $scope.product.id});
                }, 1000);
            }
        };

        $scope.isLoading = function () {
            return $scope.loading;
        };
    })
    .controller("productDetailsCtrl", function ($scope, $state, $stateParams, ProductsService) {

        $scope.product = null;

        ProductsService.getProduct($stateParams.id).then(function (product) {
            $scope.product = product;
        });

        // initial image index
        $scope.index = 0;

        // if a current image is the same as requested image
        $scope.isActive = function (index) {
            return $scope.index === index;
        };

        // show prev image
        $scope.showPrev = function () {
            $scope.index = ($scope.index > 0) ? --$scope.index : $scope.product.images.length - 1;
        };

        // show next image
        $scope.showNext = function () {
            $scope.index = ($scope.index < $scope.product.images.length - 1) ? ++$scope.index : 0;
        };

        // show a certain image
        $scope.showPhoto = function (index) {
            $scope.index = index;
        };

    });