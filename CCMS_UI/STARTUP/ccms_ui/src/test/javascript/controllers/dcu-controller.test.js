describe('dcu addition and update controllers', function() {
    var $scope, $rootScope, $controller, $httpBackend;
    var mockState, mockInform, mockConfig;

    beforeEach(module('inform'));
    beforeEach(module('dcuControllers'));
    beforeEach(module('dcuFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        mockState = { go: jasmine.createSpy('$state.go'), reload: jasmine.createSpy('$state.reload') };
        mockInform = { add: jasmine.createSpy('inform.add') };
        mockConfig = { districts: [] };
    }));

    function createAddController() {
        $httpBackend.expectGET('/scheduler/list').respond([]);
        $controller('dcuAddControllers', {
            $scope: $scope,
            $state: mockState,
            $stateParams: {},
            $modal: {},
            $location: {},
            $http: {},
            $rootScope: $rootScope,
            inform: mockInform,
            config: mockConfig
        });
        $httpBackend.flush();
    }

    it('sends a canonical gateway serial and handles a successful add', function() {
        createAddController();
        expect($scope.dcu.date).toBeDefined();
        $scope.dcu = { serial_number: 'DCU-001' };
        $httpBackend.expectPOST('/dcu/create', function(body) {
            var payload = angular.fromJson(body);
            return payload.gateway_serial_number === 'DCU-001' && payload.serial_number === 'DCU-001';
        }).respond({code: 200, message: 'Success'});

        $scope.ok();
        $httpBackend.flush();
        expect(mockState.go).toHaveBeenCalledWith('dashboard.dcu');
    });

    it('does not navigate when the API returns a business failure', function() {
        createAddController();
        $scope.dcu = { gateway_serial_number: 'DCU-002' };
        $httpBackend.expectPOST('/dcu/create').respond({code: 0, message: 'Gateway serial number is required'});

        $scope.ok();
        $httpBackend.flush();
        expect(mockState.go).not.toHaveBeenCalled();
        expect(mockInform.add).toHaveBeenCalled();
    });

    it('serializes the date picker and numeric fields before creating a DCU', function() {
        createAddController();
        $scope.dcu = {
            gateway_serial_number: 'DCU-003',
            date: {format: function(format) {
                expect(format).toBe('YYYY-MM-DD');
                return '2026-11-24';
            }},
            connected_load: '12',
            no_of_lights: '8'
        };
        $httpBackend.expectPOST('/dcu/create', function(body) {
            var payload = angular.fromJson(body);
            return payload.gateway_serial_number === 'DCU-003' &&
                payload.date === '2026-11-24' &&
                payload.connected_load === 12 &&
                payload.no_of_lights === 8;
        }).respond({code: 200, message: 'Success'});

        $scope.ok();
        $httpBackend.flush();
        expect(mockState.go).toHaveBeenCalledWith('dashboard.dcu');
    });
});
