angular.module('languageModule', ['pascalprecht.translate'])
    .factory('customLoader', function ($http, $q) {
        // return loaderFn
        return function (options) {
            var deferred = $q.defer();
            $http.get(options.prefix + options.key + options.suffix)
                .success(function (data) {
                    deferred.resolve(data);
                }).error(function (data) {
                    deferred.reject(options.key);
                });
            return deferred.promise;
        };
    })
    .config(function ($translateProvider) {
        $translateProvider
            .useLoader('customLoader', {
                prefix: '/assets/languages/',
                suffix: '.json'
            })
            .preferredLanguage('pl');
    })
    .directive('languageSwitcher', function () {
        return {
            restrict: 'E',
            scope: {
                current: '=',
                languages: '=',
                onLanguageSelect: '&'
            },
            templateUrl: '/directives/language-switcher/_language-switcher.html',
            replace: true,
            link: function (scope) {
                scope.isCurrent = function (lang) {
                    return scope.current === lang;
                };

                scope.selectLanguage = function (lang) {
                    if (!scope.isCurrent(lang)) {
                        scope.current = lang;
                        scope.onLanguageSelect({ lang: lang });
                    }
                };

                scope.getSrc = function (lang) {
                    if (scope.isCurrent(lang)) {
                        return '/assets/img/' + lang + '-active.png';
                    } else {
                        return '/assets/img/' + lang + '.png';
                    }
                }
            }
        }
    })
    .controller('languageCtrl', function ($scope, $translate) {

        $scope.currentLanguage = $translate.use();
        $scope.languages = ["pl", "en"];

        $scope.selectLanguage = function (lang) {
            $translate.use(lang);
        };

    });