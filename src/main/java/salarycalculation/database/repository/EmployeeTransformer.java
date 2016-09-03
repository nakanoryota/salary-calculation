package salarycalculation.database.repository;

import java.util.Optional;

import salarycalculation.database.CapabilityDao;
import salarycalculation.database.RoleDao;
import salarycalculation.database.model.CapabilityRecord;
import salarycalculation.database.model.EmployeeRecord;
import salarycalculation.database.model.RoleRecord;
import salarycalculation.domain.employee.BusinessDate;
import salarycalculation.domain.employee.Capability;
import salarycalculation.domain.employee.CapabilityRank;
import salarycalculation.domain.employee.Employee;
import salarycalculation.domain.employee.Role;
import salarycalculation.domain.organization.Organization;
import salarycalculation.domain.organization.OrganizationRepository;
import salarycalculation.domain.work.WorkOverTimes;
import salarycalculation.domain.work.WorkRepository;
import salarycalculation.utils.Money;
import salarycalculation.utils.PersonName;

/**
 * employeeEntityとデータ構造の差を埋めるためのトランスフォーマ.<br />
 * リポジトリ実装のユーティリティに属する（DDDではない）
 *
 * @author MASAYUKI
 */
public class EmployeeTransformer {

    private OrganizationRepository organizationRepository;
    private RoleDao roleDao;
    private CapabilityDao capabilityDao;
    private WorkRepository workRepository;

    public EmployeeTransformer() {
        this.organizationRepository = new OrganizationRepositoryDao();
        this.roleDao = new RoleDao();
        this.capabilityDao = new CapabilityDao();
        this.workRepository = new WorkRepositoryDao();
    }

    /**
     * 従業員レコードをEntityに変換する。
     *
     * @param employeeRecord
     * @return
     */
    public Employee transformToEntity(EmployeeRecord employeeRecord) {

        // 所属する組織情報を取得
        Organization organization = organizationRepository.find(employeeRecord.getOrganization());

        // 各等級情報を取得
        RoleRecord role = roleDao.get(employeeRecord.getRoleRank());
        CapabilityRecord capability = capabilityDao.get(employeeRecord.getCapabilityRank());

        //      TODO employeeはAggregateだけど、このタイミング全部持ってくるとパフォーマンスとメモリに影響でそうだからアーキテクチャを検討する必要があるかも。
        //       ex1. JPAみたいにLazyロードを検討する => FWないと実装が大変かも
        //       ex2. DomainServiceに処理を移す => 現実的な落とし所かも
        //       ex3. WorkRepositoryを作りEntityにWorkRepositoryを関数の引数に渡して取得する、
        //       EntityにRepositoryを渡すと依存が分かりづらくなるからなんとも。。。
        Optional<WorkOverTimes> works = workRepository.findByEmployeeId(employeeRecord.getNo());

        return createFromRecord(employeeRecord, organization, works, Optional.ofNullable(role),
                Optional.ofNullable(capability));

    }

    /**
     * DBレコードからEntityを生成する
     *
     * @param employeeRecord 従業員レコード
     * @param organization 組織レコード（オプション）
     * @param roleRecordOpt 役割等級（オプション）
     * @param capabilityRecordOpt 能力等級レコード（オプション）
     * @return 従業員エンティティ
     */
    Employee createFromRecord(EmployeeRecord employeeRecord, Organization organization,
            Optional<WorkOverTimes> works,
            Optional<RoleRecord> roleRecordOpt,
            Optional<CapabilityRecord> capabilityRecordOpt) {

        Employee entity = new Employee(employeeRecord.getNo());

        entity.setName(new PersonName(employeeRecord.getName()));
        entity.setBirthDay(BusinessDate.of(employeeRecord.getBirthday()));
        entity.setJoinDate(BusinessDate.of(employeeRecord.getJoinDate()));
        entity.setOrganization(organization);

        Optional<Capability> capabilityOpt = capabilityRecordOpt
                .map(e -> Capability.normal(CapabilityRank.valueOf(e.getRank()), Money.from(e.getAmount())));
        if (capabilityOpt.isPresent()) {
            entity.setCapability(capabilityOpt.get());
        }

        Optional<Role> roleOpt = roleRecordOpt.map(e -> new Role(e.getRank(), Money.from(e.getAmount())));
        if (roleOpt.isPresent()) {
            entity.setRole(roleOpt.get());
        }

        entity.setCommuteAmount(Money.from(employeeRecord.getCommuteAmount()));
        entity.setEmployeePensionAmount(Money.from(employeeRecord.getEmployeePensionAmount()));
        entity.setHealthInsuranceAmount(Money.from(employeeRecord.getHealthInsuranceAmount()));
        entity.setIncomeTaxAmount(Money.from(employeeRecord.getIncomeTaxAmount()));
        entity.setInhabitantTaxAmount(Money.from(employeeRecord.getInhabitantTaxAmount()));
        entity.setRentAmount(Money.from(employeeRecord.getRentAmount()));

        entity.setWorkOverTime1hAmount(Money.from(employeeRecord.getWorkOverTime1hAmount()));

        if (works.isPresent()) {
            entity.setWorkTimes(works.get());
        }
        return entity;
    }

    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public void setCapabilityDao(CapabilityDao capabilityDao) {
        this.capabilityDao = capabilityDao;
    }

    public void setOrganizationRepository(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }
}
