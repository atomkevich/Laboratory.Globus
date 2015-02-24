var app = angular.module("app", ["ngResource", "ngRoute"])
	.config(["$routeProvider", function($routeProvider) {
		return $routeProvider.when("/", {
			templateUrl: "/views/employees",
			controller: "EmployeeCtrl"
		}).when("/podsView", {
			templateUrl: "/views/pods",
			controller: "PODCtrl"
	    }).when("/employee/newEmployee", {
			templateUrl: "/views/newEmployee",
			controller: "EmployeeCardCtrl"
	    }).when("/employee/:id", {
            templateUrl: "/views/employee",
            controller: "EmployeeCardCtrl"
        }).when("/pod/:id", {
			templateUrl: "/views/pod",
			controller: "PODCardCtrl"
		}).when("/pod/newPOD", {
			templateUrl: "/views/newPOD",
			controller: "PODCardCtrl"
		}).when("/logout", {
			templateUrl: "/views/login",
            controller: "LogoutCtrl"
        }).otherwise({
			redirectTo: "/login"
		});
	}
	]).config([
	"$locationProvider", function($locationProvider) {
		return $locationProvider.html5Mode({
			enabled: true,
			requireBase: false
		}).hashPrefix("!"); // enable the new HTML5 routing and history API
		// return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and history API
	}
]);

app.controller("LogoutCtrl", ["$http", "$timeout", "$location", "$window", "$scope", "$resource", "$routeParams",
	function($http, $timeout, $location, $window, $scope, $resource) {
		console.log($location);
		var ss = $http({
			url:'/logout',
			method : 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		}).success(function(){
			console.log("success")
		}).error(function(err){
			console.log("error")
		});
}]);

app.controller("PODCtrl", ["$scope", "$resource","$http",  "$location", function($scope, $resource, $http, $location) {
	console.log($location);
	// the very sweet go function is inherited by all other controllers
	$scope.go = function (path) {
		$location.path(path);
	};
	var pods = $resource("/pods");
	pods.query().$promise.then(
		function( value ){
			$scope.pods = value;
		},
		function( error ){
			console.log("error")
		}
	);

	$scope.resetPODFilter = function() {
		var employees = $resource("/pods").query().$promise.then(
			function( value ){
				$scope.pods = value;
			},
			function( error ){
				console.log("error")
			});
	}
	$scope.filterPODs = function() {
		var findPOD = $resource("/pods?" + $scope.filter.param + "=" + $scope.filter.value);
		findPOD.query().$promise.then(
			function( value ){
				$scope.pods = value;
				$scope.$apply();
			},
			function( error ){
				console.log("error")
			}
		)
	};

	$scope.editPOD = function(pod) {
		pod._edit = true;
	}

	$scope.deletePOD= function(id) {
		$http({
			url:'/pods/' + id,
			method : 'DELETE',
			headers: {
				'Content-Type': 'application/json'
			},
			data:{}
		}).success(function(){
			$scope.pods = $scope.pods.filter(function(item) {
				return item.id !== id;
			});
			$location.path("/podsView");
		}).error(function(err){
			$scope.errorMessage = err;
		});
		/*	 $resource("/employees/" + id).delete(); // a RESTful-capable resource object*/

	}
	$scope.submitPOD = function(pod) {
		pod.profile.id = pod.id;
		$http({
			url:'/pods/profile',
			method : 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			data: pod.profile
		}).success(function(){
			$location.path("/podsView");
		}).error(function(err){
			$scope.errorMessage = err;
		});
		pod._edit = false;
	}
}]);



// the list controller
app.controller("EmployeeCtrl", ["$location", "$scope", "$rootScope", "$http", "$resource", "$routeParams", function($location, $scope, $rootScope, $http, $resource, $routeParams) {
	console.log($location);
	var employees = $resource("/employees");
	 employees.query().$promise.then(
		function( value ){
			$scope.employees = value;
			console.log(value)
		},
		function( error ){
			console.log("error")
		}
	);



    $resource("/current").get().$promise.then(
      function(value) {
          $rootScope.currentUser = value;
      }
    );
    $scope.resetEmployeeFilter = function() {
        var employees = $resource("/employees").query().$promise.then(
            function( value ){
                $scope.employees = value;
				console.log(value)
            },
            function( error ){
                console.log("error")
            });
    }
	$scope.filterEmployees = function() {
		var findEmployees = $resource("/employees?" + $scope.filter.param + "=" + $scope.filter.value);
		findEmployees.query().$promise.then(
			function( value ){
				$scope.employees = value;
                $scope.$apply();
			},
			function( error ){
				console.log("error")
			}
		)
	};

	$scope.editEmployee = function(empl) {
		empl._edit = true;
	}

	$scope.deleteEmployee= function(id) {
		$http({
			url:'/employees/' + id,
			method : 'DELETE',
			headers: {
				'Content-Type': 'application/json'
			},
			data:{}
		}).success(function(){
			$scope.employees = $scope.employees.filter(function(item) {
				return item.id !== id;
			});
			$location.path("/");
		}).error(function(err){
			$scope.errorMessage = err;
		});
	/*	 $resource("/employees/" + id).delete(); // a RESTful-capable resource object*/

	}
	$scope.submitEmployee = function(empl) {
		empl.profile.id = empl.id;
		$http({
			url:'/employees/profile',
			method : 'PUT',
			headers: {
				'Content-Type': 'application/json'
			},
			data: empl.profile
		}).success(function(){
			$location.path("/");
		}).error(function(err){
			$scope.errorMessage = err;
		});
        empl._edit = false;
	}


}]);



app.controller("EmployeeCardCtrl", ["$http", "$timeout", "$location", "$window", "$scope", "$resource", "$routeParams",
                            function($http, $timeout, $location, $window, $scope, $resource, $routeParams) {
	console.log($location);
	$scope.go = function (path) {
		$location.path(path);
	};

	if ($routeParams.id) {
		$scope.employee = $resource("/employees/:id", {id:"@id"}).get({id: $routeParams.id});
	}

	$scope.createEmployees = function() {
        $http({
            url:'/employees',
            method : 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            data: $scope.employee
        }).success(function(){
            $location.path("/");
            $scope.apply();
        }).error(function(err){
            $scope.errorMessage = err;
        });
	}

	$scope.editEmployee	= function() {
		$scope._edit = true;
	}
	$scope.cancelEmployee	= function() {
		$scope._edit = false;
	}

    $scope.submitEmployee = function() {
		$scope.submitEmployee = function() {
			$scope.employee.profile.id = $scope.employee.id;
			$http({
				url:'/employees/profile',
				method : 'PUT',
				headers: {
					'Content-Type': 'application/json'
				},
				data: $scope.employee.profile
			}).success(function(){
				$scope.successMessage = "Employee was success updated.";
				$scope._edit = false;
			}).error(function(err){
				$scope.errorMessage = err;
			});
		}
	}
}]);

app.controller("PODCardCtrl", ["$http", "$routeParams", "$timeout", "$location", "$scope", "$resource", "$routeParams", function($http, $timeout, $location, $scope, $resource, $routeParams) {
	if ($routeParams.id) {
		$scope.pod = $resource("/pods/:id", {id:"@id"}).get({id: $routeParams.id});
	}
	console.log($location);

	$scope.createEmployees = function() {
		$http({
			url:'/pods',
			method : 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			data: $scope.employee
		}).success(function(){
			$location.path("/");
			$scope.apply();
		}).error(function(err){
			$scope.errorMessage = err;
		});
	}

	$scope.editPOD	= function() {
		$scope._edit = true;
	}
	$scope.cancelPOD	= function() {
		$scope._edit = false;
	}

	$scope.submitPOD = function() {
		$resource("/pods/profile").update($scope.pod);
		$scope._edit = false;
	}
}]);