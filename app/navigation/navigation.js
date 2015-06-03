angular.module('navigationModule', ['ui.router', 'ngAnimate'])
    .config(function estateConfig($stateProvider, $urlRouterProvider, $locationProvider) {
        $stateProvider
            .state('home', {
                url: '/',
                views: {
                    "main": {
                        templateUrl: 'app/home/_home.html'
                    },
                    "navigation@home": {
                        templateUrl: 'app/navigation/_nav.html'
                    },
                    "content@home": {
                        templateUrl: 'app/cover/_cover.html'
                    }
                }
            })
            .state('home.contact', {
                url: 'contact',
                views: {
                    "content@home": {
                        templateUrl: 'app/contact/_contact.html'
                    }
                }
            })
            .state('home.about', {
                url: 'about',
                views: {
                    "content@home": {
                        templateUrl: 'app/about/_about.html'
                    }
                }
            })
            .state('home.products', {
                url: 'products',
                views: {
                    "content@home": {
                        templateUrl: 'app/products/_list.html',
                        controller: 'productsCtrl'
                    }
                }
            });
    })
    .controller('navigationCtrl', function ($scope, $state) {


    });