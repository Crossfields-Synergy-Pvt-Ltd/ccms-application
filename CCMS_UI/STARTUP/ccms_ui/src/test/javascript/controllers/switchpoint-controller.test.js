describe('switchpointListControllers', function() {
    var $scope, $rootScope, $controller, $httpBackend;
    var state, stateParams;

    beforeEach(module('switchpointControllers'));
    beforeEach(module('switchpointFactory'));

    beforeEach(inject(function(_$rootScope_, _$controller_, _$httpBackend_) {
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        state = { go: jasmine.createSpy('$state.go') };
        stateParams = {};
    }));

    afterEach(function() {
        try { $httpBackend.verifyNoOutstandingExpectation(); } catch (e) {}
        try { $httpBackend.verifyNoOutstandingRequest(); } catch (e) {}
    });

    function createController() {
        $controller('switchpointListControllers', {
            $scope: $scope,
            $state: state,
            $stateParams: stateParams
        });
    }

    it('loads a DCU passed through state params', function() {
        stateParams.gateway_serial_number = 'DCU-001';
        createController();

        $httpBackend.expectGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL').respond([
            { name: 'DCU 001', gateway_identifier: 'DCU-001' }
        ]);
        $httpBackend.expectGET('/dashboard/instant_data_id/DCU-001').respond({
            id: 'DCU-001', dcu_details: { gateway_serial_number: 'DCU-001' }
        });
        $httpBackend.flush();

        expect($scope.selected_dcu.gateway_identifier).toBe('DCU-001');
        expect($scope.obj.id).toBe('DCU-001');
    });

    it('clears stale data and reports a load error', function() {
        createController();
        $httpBackend.expectGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL').respond([]);
        $httpBackend.flush();

        $scope.selected_dcu = { name: 'DCU 001', gateway_identifier: 'DCU-001' };
        $scope.obj = { id: 'OLD' };
        $httpBackend.expectGET('/dashboard/instant_data_id/DCU-001').respond(500);
        $scope.view();

        expect($scope.obj).toBe(null);
        $httpBackend.flush();
        expect($scope.error_message).toBe('Unable to load switch point information.');
        expect($scope.loading).toBe(false);
    });

    it('passes only the DCU serial number to edit navigation', function() {
        createController();
        $httpBackend.expectGET('/dcu/dcu_name_list?district=ALL&mandal=ALL&gp=ALL').respond([]);
        $httpBackend.flush();

        $scope.modifydcu({ dcu_details: { gateway_serial_number: 'DCU-001' } });
        expect(state.go).toHaveBeenCalledWith('dashboard.dcu_edit', {
            gateway_serial_number: 'DCU-001'
        });
    });
});
