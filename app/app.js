angular.module('klaudiabis', ['ngRoute', 'ui.materialize', 'pascalprecht.translate', 'languageModule', 'productsModule', 'metricsModule'])
    .config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/', {
                    templateUrl: 'app/home/_home.html',
                    controller: 'ProductsCtrl'
                }).
                when('/product/:productId', {
                    templateUrl: 'app/products/_details.html',
                    controller: 'ProductDetailCtrl'
                }).
                otherwise({
                    redirectTo: '/'
                });
        }]).controller('AppCtrl', function HomeCtrl($scope) {
//        $state.go('home');
//        $scope.$state = $state;
//        Analytics.trackPage('/');

        $scope.menu = [
            {
                link: '#products',
                title: 'HOME.PRODUCTS',
                icon: 'dashboard'
            },
            {
                link: '#about',
                title: 'HOME.ABOUT',
                icon: 'group'
            },
            {
                link: '#contact',
                title: 'HOME.CONTACT',
                icon: 'message'
            }
        ];
        $scope.admin = [
            {
                link: '',
                title: 'Trash',
                icon: 'delete'
            },
            {
                link: 'showListBottomSheet($event)',
                title: 'Settings',
                icon: 'settings'
            }
        ];

        $scope.loading = true;
    });