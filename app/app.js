angular.module('klaudiabis', ['ngMaterial', 'ngMdIcons', 'ui.router', 'pascalprecht.translate', 'ngAnimate', 'navigationModule', 'languageModule', 'productsModule', 'contactModule', 'angular-google-analytics'])
    .config(function (AnalyticsProvider, $mdThemingProvider) {
        AnalyticsProvider.setAccount('UA-54287525-1');
        AnalyticsProvider.trackPages(false);
        AnalyticsProvider.useAnalytics(true);

        $mdThemingProvider.theme('default')
            .primaryPalette('orange')
            .accentPalette('brown');
    })
    .controller('AppCtrl', function HomeCtrl($scope, $mdBottomSheet, $mdSidenav, $mdDialog) {
//        $state.go('home');
//        $scope.$state = $state;
//        Analytics.trackPage('/');
        $scope.toggleSidenav = function(menuId) {
            $mdSidenav(menuId).toggle();
          };


    })
    .directive("scroll", function ($window) {
        return function (scope, element, attrs) {
            angular.element($window).bind("scroll", function () {
                if (this.pageYOffset >= 100) {
                    scope.fillNavigation = true;
                } else {
                    scope.fillNavigation = false;
                }
                scope.$apply();
            });
        };
    });