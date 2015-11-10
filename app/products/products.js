angular.module('productsModule', [])
    .directive('productsLoader', function () {
        return {
            restrict: 'E',
            scope: {
                loading: '='
            },
            templateUrl: 'directives/products-loader/_products-loader.html',
            replace: true
        }
    }).factory('ProductsService', function ($http, $q) {
        return {
            productList: null,

            productsLoaded: function () {
                return !!self.productList;
            },

            getProducts: function () {
                if (this.productsLoaded()) {
                    console.log("Returning previously loaded prods");
                    return $q.when(self.productList);
                } else {
                    return $http.get('api/products/').then(function (response) {
                        self.productList = response.data;
                        return self.productList;
                    });
                }
            },

            getProduct: function (productId) {

                if (!this.productsLoaded()) {
                    return $q.when(null);
                } else {
                    console.log("loading prods...");
                    return $q.when(self.productList.filter(function (elem) {
                        return elem.id == productId;
                    }));
                }
            },

            getProductPictures: function (productId) {
                return $http.get('api/product/' + productId + '/pictures').then(function (response) {
                    return response.data;
                });
            }
        };
    }).controller('ProductsCtrl', function ($scope, ProductsService) {

        var pageSize = 1;

        var pagesShown = 1;

        $scope.loading = true;

        //Analytics.trackPage('/products');
        $scope.products = [];

        function pairs(arr) {
            var pairs = [];

            for (var i = 0; i < arr.length; i = i + 2) {
                pairs.push(arr.slice(i, i + 2));
            }
            return pairs;
        }

        //Init
        $('.parallax').parallax();
        $('.scrollspy').scrollSpy();

        ProductsService.getProducts().then(function (productList) {
            $scope.products = pairs(productList);
            $scope.loading = false;
        });

        $scope.paginationLimit = function (data) {
            return pageSize * pagesShown;
        };

        $scope.hasMoreItemsToShow = function () {
            return pagesShown < ($scope.products.length / pageSize);
        };

        $scope.showMoreItems = function () {
            pagesShown = pagesShown + 1;
        };

    }).controller("ProductCtrl", function ($scope, $location, ProductsService, MetricsService) {

        $scope.imagesLoading = false;
        $scope.pictures = [];
        $scope.views = 0;

        $scope.loadPictures = function () {
            if (!$scope.imagesLoading && $scope.pictures.length === 0) {
                $scope.imagesLoading = true;
                ProductsService.getProductPictures($scope.prod.productId.id).then(function (pictureList) {
                    $scope.pictures = pictureList;
                    $scope.imagesLoading = false;
                });
            }
        };

        $scope.selectProduct = function () {
            $location.path("/product/" + $scope.prod.productId.id);
        };

        $scope.isLoading = function () {
            return $scope.loading;
        };

        MetricsService.getProductMetric($scope.prod.productId.id).then(function (count) {
            return $scope.views = count;
        });
    });
/*.controller("productDetailsCtrl", function ($scope, $state, $stateParams, ProductsService, Analytics) {

 $scope.product = null;

 ProductsService.getProduct($stateParams.id).then(function (product) {
 if (product && product.length > 0) {
 $scope.product = product[0];
 Analytics.trackPage('/product/' + $scope.product.name);
 } else {
 $state.go('home.products');
 }
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

 });*/